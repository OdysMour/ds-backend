
package gr.odys.ds_backend.service;
import gr.odys.ds_backend.entity.Citizen;
/*
import com.github.javafaker.Faker;
import gr.odys.ds_backend.entity.Course;
*/
import gr.odys.ds_backend.entity.Role;
/*
import gr.odys.ds_backend.entity.Student;
*/
import gr.odys.ds_backend.entity.User;
import gr.odys.ds_backend.entity.UserProfile;
import gr.odys.ds_backend.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;


/**
 * Service to populate database with initial data.
 */

@Service
public class InitialDataService {

    private static final int LAST_STUDENT_ID = 10;
    private static final int LAST_COURSE_ID = 10;
    private static final int LAST_STUDENT_COURSE_ID = 10;

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final CitizenRepository citizenRepository;
    private final RoleRepository roleRepository;
/*
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
*/
    private final PasswordEncoder passwordEncoder;

    public InitialDataService(UserRepository userRepository,
                              RoleRepository roleRepository,
                              UserProfileRepository userProfileRepository,
                              CitizenRepository citizenRepository,
/*
                              StudentRepository studentRepository,
                              CourseRepository courseRepository,
                              AssignmentRepository assignmentRepository,
*/
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userProfileRepository = userProfileRepository;
        this.citizenRepository = citizenRepository;
/*
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
*/
        this.passwordEncoder = passwordEncoder;
    }

    private void createUsersAndRoles() {
        final List<String> rolesToCreate = List.of("ROLE_ADMIN", "ROLE_VET", "ROLE_USER", "ROLE_EMPLOYEE");
        for (final String roleName : rolesToCreate) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                roleRepository.save(new Role(roleName));
                return null;
            });
        }

        this.userRepository.findByUsername("user").orElseGet(() -> {
            User user = new User("user", "user@hua.gr", this.passwordEncoder.encode("12345678"));
            Set<Role> roles = new HashSet<>();
            roles.add(this.roleRepository.findByName("ROLE_USER").orElseThrow());
            user.setRoles(roles);
            UserProfile profile = new UserProfile();
            Citizen citizen = new Citizen();
            citizen.setFirstName("User");
            citizen.setLastName("User");
            citizen.setPhone(1234567890L);
            citizen.setCity("Athens");
            citizenRepository.save(citizen);
            profile.setCitizen(citizen);
            userProfileRepository.save(profile);
            user.setProfile(profile);
            userRepository.save(user);
            return null;
        });
        this.userRepository.findByUsername("vet").orElseGet(() -> {
            User user = new User("vet", "vet@hua.gr", this.passwordEncoder.encode("12345678"));
            Set<Role> roles = new HashSet<>();
            roles.add(this.roleRepository.findByName("ROLE_VET").orElseThrow());
            user.setRoles(roles);
            UserProfile profile = new UserProfile();
            Citizen citizen = new Citizen();
            citizen.setFirstName("Vet");
            citizen.setLastName("Vet");
            citizen.setPhone(1234567890L);
            citizen.setCity("Athens");
            citizenRepository.save(citizen);
            profile.setCitizen(citizen);
            userProfileRepository.save(profile);
            user.setProfile(profile);

            userRepository.save(user);
            return null;
        });
        this.userRepository.findByUsername("admin").orElseGet(() -> {
            User user = new User("admin", "admin@hua.gr", this.passwordEncoder.encode("12345678"));
            Set<Role> roles = new HashSet<>();
            roles.add(this.roleRepository.findByName("ROLE_USER").orElseThrow());
            roles.add(this.roleRepository.findByName("ROLE_EMPLOYEE").orElseThrow());
            roles.add(this.roleRepository.findByName("ROLE_ADMIN").orElseThrow());
            roles.add(this.roleRepository.findByName("ROLE_VET").orElseThrow());
            user.setRoles(roles);
            UserProfile profile = new UserProfile();
            Citizen citizen = new Citizen();
            citizen.setFirstName("Admin");
            citizen.setLastName("Admin");
            citizen.setPhone(1234567890L);
            citizen.setCity("Athens");
            citizenRepository.save(citizen);
            profile.setCitizen(citizen);
            userProfileRepository.save(profile);
            user.setProfile(profile);
            userRepository.save(user);
            return null;
        });
        this.userRepository.findByUsername("employee").orElseGet(() -> {
            User user = new User("employee", "employee@hua.gr", this.passwordEncoder.encode("12345678"));
            Set<Role> roles = new HashSet<>();
            roles.add(this.roleRepository.findByName("ROLE_EMPLOYEE").orElseThrow());
            user.setRoles(roles);
            UserProfile profile = new UserProfile();
            Citizen citizen = new Citizen();
            citizen.setFirstName("Employee");
            citizen.setLastName("Employee");
            citizen.setPhone(1234567890L);
            citizen.setCity("Athens");
            citizenRepository.save(citizen);
            profile.setCitizen(citizen);
            userProfileRepository.save(profile);
            user.setProfile(profile);
            userRepository.save(user);
            return null;
        });
    }

/*
    private void createStudents() {
        for (int i=1; i<=LAST_STUDENT_ID; i++) {
            final Faker faker = new Faker(new Random(i));
            final String firstName = faker.name().firstName();
            final String lastName = faker.name().lastName();
            final String email = faker.internet().emailAddress();

            this.studentRepository.findByEmail(email).orElseGet(() -> {
                Student student = new Student();
                student.setFirstName(firstName);
                student.setLastName(lastName);
                student.setEmail(email);
                this.studentRepository.save(student);
                return null;
            });
        }
    }
*/

/*
    private void createCourses() {
        for (int i=1; i<=LAST_COURSE_ID; i++) {
            final String title = "Course " + i;

            this.courseRepository.findByTitle(title).orElseGet(() -> {
                Course course = new Course();
                course.setTitle(title);
                this.courseRepository.save(course);
                return null;
            });
        }
    }
*/

/*
    private void createStudentsCourses() {
        final List<Student> studentList = this.studentRepository.findAll();
        for (Student student : studentList) {
            final Faker faker = new Faker(new Random(student.getId()));

            final List<Integer> randomCourseIdList = Stream.of(
                    faker.number().numberBetween(1, LAST_COURSE_ID),
                    faker.number().numberBetween(1, LAST_COURSE_ID),
                    faker.number().numberBetween(1, LAST_COURSE_ID),
                    faker.number().numberBetween(1, LAST_COURSE_ID),
                    faker.number().numberBetween(1, LAST_COURSE_ID)
            ).distinct().toList();

            final List<Course> newCourseList = new ArrayList<>();
            for (final int courseId : randomCourseIdList) {
                Course course = this.courseRepository.findById(courseId).orElse(null);
                if (course == null) {
                    System.out.println("Course with ID " + courseId + " does not exist!");
                    continue;
                }
                newCourseList.add(course);
            }

            student.setCourses(newCourseList);
            this.studentRepository.save(student);
        }
    }
*/

    @PostConstruct
    public void setup() {
        this.createUsersAndRoles();
/*
        this.createCourses();
        this.createStudents();
        this.createStudentsCourses();
*/
    }
}
