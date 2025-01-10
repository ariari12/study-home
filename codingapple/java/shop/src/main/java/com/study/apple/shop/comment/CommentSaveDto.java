package com.study.apple.shop.comment;

import jakarta.persistence.Column;
import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CommentSaveDto {
    private String username;
    private String content;
    private Long parentId;
}
