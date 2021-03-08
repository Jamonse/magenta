package com.jsoft.magenta;

import com.jsoft.magenta.security.dao.PrivilegeRepository;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.users.ColorTheme;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import com.jsoft.magenta.worktimes.WorkTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserSetupLoader implements ApplicationListener<ApplicationContextEvent>
{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PrivilegeRepository privilegeRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent)
    {
        User admin = this.userRepository.findByEmail("admin@admin.com").orElse(null);
        if(admin == null)
        {
            Privilege privilege = new Privilege();
            privilege.setName("account");
            privilege.setLevel(AccessPermission.ADMIN);
            Privilege privilege1 = new Privilege();
            privilege1.setName("project");
            privilege1.setLevel(AccessPermission.ADMIN);
            Privilege privilege2 = new Privilege();
            privilege2.setName("post");
            privilege2.setLevel(AccessPermission.ADMIN);
            Privilege privilege3 = new Privilege();
            privilege3.setName("user");
            privilege3.setLevel(AccessPermission.ADMIN);
            this.privilegeRepository.save(privilege);
            this.privilegeRepository.save(privilege1);
            this.privilegeRepository.save(privilege2);
            this.privilegeRepository.save(privilege3);
            Set<Privilege> privileges = Set.of(privilege, privilege1, privilege2, privilege3);
            String password = passwordEncoder.encode("password");
            User user = new User(1L, "admin", "admin", "admin@admin.com", "phoneNumber",
                    password, "image", true, ColorTheme.LIGHT, LocalDate.now(), LocalDate.now(),
                    new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), privileges, new HashSet<>());

            userRepository.save(user);
        }
    }
}
