package com.satuduatiga.api.blog.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BlogRequest {

    @NotBlank(message = "title must not be blank")
    private String title;

    @NotBlank(message = "content must not be blank")
    private String content;

    @NotEmpty(message = "topics must not be blank")
    private Set<@Pattern(regexp = "^[a-zA-Z ]*$", message = "Special character and number are not allowed") @NotBlank(message = "topic must not be blank") String> topics;

}
