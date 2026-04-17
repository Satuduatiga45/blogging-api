package com.satuduatiga.api.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotBlank(message = "category must not be blank")
    private String category;

    @NotEmpty(message = "tags must not be blank")
    private List<@NotBlank(message = "tag must not be blank") String> tags;

}
