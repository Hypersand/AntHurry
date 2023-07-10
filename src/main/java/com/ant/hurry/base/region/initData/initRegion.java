package com.ant.hurry.base.region.initData;

import com.ant.hurry.base.region.service.RegionSearchService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile( {"dev, prod"} )
public class initRegion {

    @Bean
    CommandLineRunner initData(RegionSearchService regionSearchService){
        return new CommandLineRunner() {
            @Override
            @Transactional
            public void run(String... args) throws Exception {
                regionSearchService.selectPattern();
            }
        };
    }
}
