package com.buixuanphat.spot_on.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dnbno2tkc",
                "api_key", "896257422881191",
                "api_secret", "X6BnafHH4o_-bojDL2gPXzPHQDE",
                "secure", true
        ));
    }
}
