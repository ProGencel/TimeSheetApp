import {
  Component,
  ElementRef,
  Input,
  ViewChild,
  AfterViewInit,
  OnDestroy,
  OnChanges,
  SimpleChanges,
  ChangeDetectionStrategy,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { VERTEX_SHADER, FRAGMENT_SHADER } from './animated-gradient.shader';
import {
  GradientConfig,
  NoiseConfig,
  PresetParams,
  PatternShapes,
  resolveParams,
  hexToRgba,
} from './animated-gradient.types';

@Component({
  selector: 'app-animated-gradient',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div
      #container
      class="absolute inset-0 overflow-hidden"
      [style.borderRadius]="radius"
    >
      <canvas #canvas style="display:block; width:100%; height:100%;"></canvas>

      <div
        *ngIf="noise && noise.opacity > 0"
        style="position:absolute; inset:0; pointer-events:none;"
        [style.backgroundImage]="noiseDataUrl"
        [style.backgroundSize.px]="(noise.scale ?? 1) * 200"
        style="background-repeat:repeat;"
        [style.opacity]="noise.opacity / 2"
      ></div>

      <div *ngIf="hasWebGLError" class="absolute inset-0 bg-neutral-900"></div>
    </div>
  `,
})
export class AnimatedGradient implements AfterViewInit, OnChanges, OnDestroy {
  @Input() config: GradientConfig = { preset: 'Aurora' };
  @Input() noise?: NoiseConfig;
  @Input() radius = '0px';

  @ViewChild('canvas', { static: true }) canvasRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('container', { static: true }) containerRef!: ElementRef<HTMLDivElement>;

  hasWebGLError = false;

  // Aynı orijinal component'teki noise base64 PNG'si
  readonly noiseDataUrl =
    'url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwBAMAAAClLOS0AAAAElBMVEUAAAAAAAAAAAAAAAAAAAAAAADgKxmiAAAABnRSTlMCCgkGBAVJOAVJAAAASklEQVQ4y2NgGAWjYBSMglEwCgY/YGRgZBQUYmJiZGQEkYwMjIyMgoKCjIyMIJKBgRFIMjIyAklGRkYGRkFBYEcwMDIyMjAOUQAA1I4HwVwZAkYAAAAASUVORK5CYII=")';

  private gl: WebGL2RenderingContext | null = null;
  private program: WebGLProgram | null = null;
  private vertexShader: WebGLShader | null = null;
  private fragmentShader: WebGLShader | null = null;
  private positionBuffer: WebGLBuffer | null = null;
  private resizeObserver: ResizeObserver | null = null;
  private frameId: number | undefined;
  private startTime = 0;
  private params: PresetParams = resolveParams(this.config);

  private uniforms: Record<string, WebGLUniformLocation | null> = {};

  ngAfterViewInit(): void {
    this.params = resolveParams(this.config);
    this.initWebGL();
  }

  // React'teki useMemo(() => resolveParams(config), [config]) burada karşılığı:
  // @Input değiştiğinde params'ı yeniden hesaplıyoruz.
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['config'] && !changes['config'].firstChange) {
      this.params = resolveParams(this.config);
    }
  }

  ngOnDestroy(): void {
    this.cleanup();
  }

  private initWebGL(): void {
    const canvas = this.canvasRef.nativeElement;
    const container = this.containerRef.nativeElement;

    try {
      const gl = canvas.getContext('webgl2', {
        premultipliedAlpha: true,
        alpha: true,
        antialias: true,
      });

      if (!gl) {
        this.hasWebGLError = true;
        return;
      }
      this.gl = gl;

      this.vertexShader = this.compileShader(gl, gl.VERTEX_SHADER, VERTEX_SHADER);
      if (!this.vertexShader) {
        this.hasWebGLError = true;
        return;
      }

      this.fragmentShader = this.compileShader(gl, gl.FRAGMENT_SHADER, FRAGMENT_SHADER);
      if (!this.fragmentShader) {
        this.cleanup();
        this.hasWebGLError = true;
        return;
      }

      const program = gl.createProgram()!;
      gl.attachShader(program, this.vertexShader);
      gl.attachShader(program, this.fragmentShader);
      gl.linkProgram(program);

      if (!gl.getProgramParameter(program, gl.LINK_STATUS)) {
        gl.deleteProgram(program);
        this.cleanup();
        this.hasWebGLError = true;
        return;
      }
      this.program = program;
      gl.useProgram(program);

      this.positionBuffer = gl.createBuffer();
      gl.bindBuffer(gl.ARRAY_BUFFER, this.positionBuffer);
      gl.bufferData(
        gl.ARRAY_BUFFER,
        new Float32Array([-1, -1, 1, -1, -1, 1, -1, 1, 1, -1, 1, 1]),
        gl.STATIC_DRAW
      );

      const positionLocation = gl.getAttribLocation(program, 'a_position');
      gl.enableVertexAttribArray(positionLocation);
      gl.vertexAttribPointer(positionLocation, 2, gl.FLOAT, false, 0, 0);

      this.uniforms = {
        u_time: gl.getUniformLocation(program, 'u_time'),
        u_resolution: gl.getUniformLocation(program, 'u_resolution'),
        u_pixelRatio: gl.getUniformLocation(program, 'u_pixelRatio'),
        u_scale: gl.getUniformLocation(program, 'u_scale'),
        u_rotation: gl.getUniformLocation(program, 'u_rotation'),
        u_color1: gl.getUniformLocation(program, 'u_color1'),
        u_color2: gl.getUniformLocation(program, 'u_color2'),
        u_color3: gl.getUniformLocation(program, 'u_color3'),
        u_proportion: gl.getUniformLocation(program, 'u_proportion'),
        u_softness: gl.getUniformLocation(program, 'u_softness'),
        u_shape: gl.getUniformLocation(program, 'u_shape'),
        u_shapeScale: gl.getUniformLocation(program, 'u_shapeScale'),
        u_distortion: gl.getUniformLocation(program, 'u_distortion'),
        u_swirl: gl.getUniformLocation(program, 'u_swirl'),
        u_swirlIterations: gl.getUniformLocation(program, 'u_swirlIterations'),
      };

      const resize = () => {
        const width = container.clientWidth;
        const height = container.clientHeight;
        const pixelRatio = window.devicePixelRatio || 1;
        canvas.width = width * pixelRatio;
        canvas.height = height * pixelRatio;
        canvas.style.width = `${width}px`;
        canvas.style.height = `${height}px`;
        gl.viewport(0, 0, canvas.width, canvas.height);
      };

      resize();
      this.resizeObserver = new ResizeObserver(resize);
      this.resizeObserver.observe(container);

      this.startTime = performance.now();
      this.frameId = requestAnimationFrame(this.animate);
    } catch {
      this.hasWebGLError = true;
    }
  }

  // Arrow function kullandık ki 'this' bağlamı requestAnimationFrame içinde kaybolmasın
  // (React'teki closure üzerinden params'a erişim burada instance property üzerinden yapılıyor)
  private animate = (time: number): void => {
    const gl = this.gl;
    if (!gl || !this.canvasRef) return;

    const canvas = this.canvasRef.nativeElement;
    const elapsed = (time - this.startTime) / 1000;
    const speed = (this.params.speed / 100) * 5;

    gl.uniform1f(this.uniforms['u_time'], elapsed * speed + this.params.offset * 0.01);
    gl.uniform2f(this.uniforms['u_resolution'], canvas.width, canvas.height);
    gl.uniform1f(this.uniforms['u_pixelRatio'], window.devicePixelRatio || 1);
    gl.uniform1f(this.uniforms['u_scale'], this.params.scale);
    gl.uniform1f(this.uniforms['u_rotation'], (this.params.rotation * Math.PI) / 180);

    const c1 = hexToRgba(this.params.color1);
    const c2 = hexToRgba(this.params.color2);
    const c3 = hexToRgba(this.params.color3);
    gl.uniform4f(this.uniforms['u_color1'], c1[0], c1[1], c1[2], c1[3]);
    gl.uniform4f(this.uniforms['u_color2'], c2[0], c2[1], c2[2], c2[3]);
    gl.uniform4f(this.uniforms['u_color3'], c3[0], c3[1], c3[2], c3[3]);

    gl.uniform1f(this.uniforms['u_proportion'], this.params.proportion / 100);
    gl.uniform1f(this.uniforms['u_softness'], this.params.softness / 100);
    gl.uniform1f(this.uniforms['u_shape'], PatternShapes[this.params.shape]);
    gl.uniform1f(this.uniforms['u_shapeScale'], this.params.shapeSize / 100);
    gl.uniform1f(this.uniforms['u_distortion'], this.params.distortion / 50);
    gl.uniform1f(this.uniforms['u_swirl'], this.params.swirl / 100);
    gl.uniform1f(
      this.uniforms['u_swirlIterations'],
      this.params.swirl === 0 ? 0 : this.params.swirlIterations
    );

    gl.drawArrays(gl.TRIANGLES, 0, 6);
    this.frameId = requestAnimationFrame(this.animate);
  };

  private compileShader(
    gl: WebGL2RenderingContext,
    type: number,
    source: string
  ): WebGLShader | null {
    const shader = gl.createShader(type)!;
    gl.shaderSource(shader, source);
    gl.compileShader(shader);
    if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
      gl.deleteShader(shader);
      return null;
    }
    return shader;
  }

  private cleanup(): void {
    if (this.frameId !== undefined) {
      cancelAnimationFrame(this.frameId);
      this.frameId = undefined;
    }
    this.resizeObserver?.disconnect();
    this.resizeObserver = null;

    if (this.gl) {
      if (this.program) this.gl.deleteProgram(this.program);
      if (this.vertexShader) this.gl.deleteShader(this.vertexShader);
      if (this.fragmentShader) this.gl.deleteShader(this.fragmentShader);
      if (this.positionBuffer) this.gl.deleteBuffer(this.positionBuffer);
    }
  }
}
