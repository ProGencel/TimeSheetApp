package com.aksigorta.timesheet.service;

import com.aksigorta.timesheet.model.project.Project;
import com.aksigorta.timesheet.model.project.ProjectResponseDto;
import com.aksigorta.timesheet.model.project.ProjectSaveDto;
import com.aksigorta.timesheet.model.user.User;
import com.aksigorta.timesheet.repository.ProjectRepository;
import com.aksigorta.timesheet.repository.UserRepository;
import com.aksigorta.timesheet.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    private Long getCurrentUserId()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return customUserDetails.getUserId();
    }

    public ResponseEntity<?> saveProject(ProjectSaveDto projectSaveDto)
    {
        Optional<Project> projectOptional = projectRepository.findByName(projectSaveDto.getName());

        if(projectOptional.isPresent())
        {
            return ResponseEntity.badRequest().body(Map.of("Success",false,"Error", "You can't use a name that's already taken by another project"));
        }
        else
        {
            Project project = modelMapper.map(projectSaveDto,Project.class);
            User user = userRepository.findById(getCurrentUserId()).get();
            project.setUser(user);
            projectRepository.save(project);

            return ResponseEntity.ok().body(Map.of("Success",true));
        }
    }

    public Page<Project> searchProject(String q,int page)
    {
        Pageable pageable = PageRequest.of(page,10);
        Page<Project> projectPage = projectRepository.findByNameContainsIgnoreCase(q,pageable);

        return projectPage;
    }

    public ResponseEntity<?> get(Long id)
    {
        Optional<Project> projectOptional = projectRepository.findById(id);

        if(projectOptional.isEmpty())
        {
            return ResponseEntity.badRequest().body(Map.of("Success",false,"Error Message:","Project cannot find"));
        }
        else
        {
            return ResponseEntity.ok().body(projectOptional.get());
        }
    }

    public boolean isOwner(Long projectId)
    {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if(projectOptional.isPresent())
        {
            return Objects.equals(projectOptional.get().getUser().getId(), getCurrentUserId());
        }
        return false;
    }

    public ResponseEntity<?> setFinished(Long projectId)
    {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if(projectOptional.isPresent())
        {
            if(isOwner(projectId))
            {
                Project project = projectOptional.get();
                project.setFinished(true);
                projectRepository.save(project);
                return ResponseEntity.ok().body(Map.of("Success", true));
            }
            else
            {
                return ResponseEntity.badRequest().body(Map.of("Success",false,"Error Message:","You cannot check a project that is not belong to you"));
            }
        }
        return ResponseEntity.badRequest().body(Map.of("Success",false,"Error Message:","Project cannot find"));
    }

    private Page<ProjectResponseDto> getProjectResponsePage(Page<Project> projectPage)
    {
        Page<ProjectResponseDto> projectResponseDtoPage = projectPage.
                map((element) -> modelMapper.map(element, ProjectResponseDto.class));

        return projectResponseDtoPage;
    }
}
