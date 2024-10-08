package com.example.RegisterLogin.EmployeeController;

import com.example.RegisterLogin.Dto.*;
import com.example.RegisterLogin.Entity.Education;
import com.example.RegisterLogin.Entity.Employee;
import com.example.RegisterLogin.Repo.EducationRepo;
import com.example.RegisterLogin.Repo.InstitutionRepo;
import com.example.RegisterLogin.Service.EmployeeService;
import com.example.RegisterLogin.Service.JwtService;
import com.example.RegisterLogin.response.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api/v1/employee")


public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    private final JwtService jwtService;
    @Autowired
    private EducationRepo educationRepo;

    @Autowired
    private InstitutionRepo institutionRepo;
    public EmployeeController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping(path = "/save")
    public ResponseEntity<Employee> saveEmployee(@RequestBody EmployeeDTO employeeDTO)
    {

      Employee registeredUser  = employeeService.addEmployee(employeeDTO);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> logingEmployee(@RequestBody LoginDTO loginDTO)
    {
        Employee authenticatedUser = employeeService.authenticate(loginDTO);

        LoginResponse loginResponse = employeeService.loginEmployee(loginDTO);

        return ResponseEntity.ok(loginResponse);
    }


    @GetMapping(path= "/auth/users")
    public ResponseEntity<List<Employee>> allUsers() {
        List<Employee> users = employeeService.allUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping(path="/auth/users/me")
    public ResponseEntity<EmployeeDTO> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Employee currentUser = (Employee) authentication.getPrincipal();
        // Map Employee entity to EmployeeDTO
        EmployeeDTO employeeDTO = new EmployeeDTO(
                currentUser.getEmployeeid(),
                currentUser.getEmployeename(),

                currentUser.getEmail(),
                currentUser.getPassword(),
                currentUser.getEmployeelastname(),
                currentUser.getPhone(), null

                 // Optionally map Education to EducationDTO if needed
        );
        return ResponseEntity.ok(employeeDTO);
    }


    @PostMapping(path = "/auth/users/me/update", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfile(
            @RequestParam("employee") String employeeJson, // employee details as JSON string
            @RequestParam(value = "photo", required = false) MultipartFile photo // photo as file
    ) {
        // Parse the employee JSON string into EmployeeDTO
        ObjectMapper objectMapper = new ObjectMapper();
        EmployeeDTO updateProfileRequest;
        try {
            updateProfileRequest = objectMapper.readValue(employeeJson, EmployeeDTO.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid employee data");
        }

        // Get the current user from the authentication context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = (Employee) authentication.getPrincipal();

        // Update employee profile and photo
        UpdateEmployeeResponse updatedUser = employeeService.updateEmployee(updateProfileRequest, photo, currentUser);

        return ResponseEntity.ok(updatedUser);
    }
    // Educations endpoints
    @PostMapping(path = "/auth/add/educations") // Changed path to "educations" to reflect multiple entries
    public ResponseEntity<List<EducationResponse>> addOrUpdateEducationsForCurrentUser(@RequestBody List<EducationDTO> educationDTOs) {
        // Get the current authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = (Employee) authentication.getPrincipal();

        // Call the service method to handle education updates and get the saved entities
        List<EducationResponse> savedEducations = employeeService.addOrUpdateEducations(educationDTOs, currentUser);

        // Return the list of saved educations
        return ResponseEntity.ok(savedEducations);
    }

    @GetMapping(path = "/auth/me/educations")
    public ResponseEntity<List<EducationResponse>> getAllEducationsForCurrentUser() {
        // Get the current authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = (Employee) authentication.getPrincipal();
        // Call the service method to get the list of education for the current user
        List<EducationResponse> educationList = employeeService.getAllEducationsForEmployee(currentUser);

        // Return the list of educations as the response
        return ResponseEntity.ok(educationList);
    }

    //Experience endpoint one for inserting to db and one for get from it

    @PostMapping(path = "/auth/add/experience") // Changed path to "educations" to reflect multiple entries
    public ResponseEntity<List<ExperienceResponse>> addOrUpdateExperienceForCurrentUser(@RequestBody List<ExperienceDTO> experienceDTOS) {
        // Get the current authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = (Employee) authentication.getPrincipal();

        // Call the service method to handle education updates and get the saved entities
        List<ExperienceResponse> savedExperience= employeeService.addOrUpdateExperience(experienceDTOS, currentUser);

        // Return the list of saved educations
        return ResponseEntity.ok(savedExperience);
    }
/// Nea aggelia gia douleia

    @PostMapping(path = "/auth/add/ads") // Changed path to "educations" to reflect multiple entries
    public ResponseEntity<AdsDTO> addJobAd(@RequestBody AdsDTO adsDTOS) {
        // Get the current authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = (Employee) authentication.getPrincipal();

        // Call the service method to handle education updates and get the saved entities
        AdsDTO savedJobAd= employeeService.createJobAd(adsDTOS, currentUser);

        // Return the list of saved educations
        return ResponseEntity.ok(savedJobAd);
    }

    @GetMapping(path = "/auth/me/experience")
    public ResponseEntity<List<ExperienceResponse>> getAllExperienceForCurrentUser() {
        // Get the current authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = (Employee) authentication.getPrincipal();
        // Call the service method to get the list of education for the current user
        List<ExperienceResponse> experienceList = employeeService.getAllExperienceForEmployee(currentUser);

        // Return the list of educations as the response
        return ResponseEntity.ok(experienceList);
    }



    @PostMapping("/auth/add/skills")
    public ResponseEntity<List<SkillResponse>> addOrUpdateSkillsForCurrentUser(@RequestBody List<Integer> skillIds) {
        // Get the current authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = (Employee) authentication.getPrincipal();

        // Call the service method to handle skill updates and get the saved entities
        List<SkillResponse> savedSkills = employeeService.addOrUpdateSkills(skillIds, currentUser);

        // Return the list of saved skills
        return ResponseEntity.ok(savedSkills);
    }

    @GetMapping(path = "/auth/me/skills")
    public ResponseEntity<List<SkillResponse>> getAllSkillsForCurrentUser() {
        // Get the current authenticated user from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Employee currentUser = (Employee) authentication.getPrincipal();

        // Call the service method to get the list of skills for the current user
        List<SkillResponse> skillList = employeeService.getAllSkillsForEmployee(currentUser);

        // Return the list of skills as the response
        return ResponseEntity.ok(skillList);
    }
}
