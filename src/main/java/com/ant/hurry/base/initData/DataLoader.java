package com.ant.hurry.base.initData;

import com.ant.hurry.boundedContext.member.entity.Role;
import com.ant.hurry.boundedContext.member.entity.RoleType;
import com.ant.hurry.boundedContext.member.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName(RoleType.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role(RoleType.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }

        if (roleRepository.findByName(RoleType.ROLE_MEMBER).isEmpty()) {
            Role memberRole = new Role(RoleType.ROLE_MEMBER);
            roleRepository.save(memberRole);
        }
    }

}
