package com.jsoft.magenta;

import com.jsoft.magenta.security.PrivilegeRepository;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.users.ColorTheme;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
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
            this.privilegeRepository.save(privilege);
            this.privilegeRepository.save(privilege1);
            this.privilegeRepository.save(privilege2);
            Set<Privilege> privileges = Set.of(privilege, privilege1, privilege2);
            String password = passwordEncoder.encode("password");
            User user = new User(1L, "admin", "admin", "admin@admin.com", "phoneNumber",
                    password, "image", true, ColorTheme.LIGHT, LocalDate.now(), LocalDate.now(),
                    new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), privileges);

            userRepository.save(user);
        }
    }
}
