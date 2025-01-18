package com.study.apple.shop.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/comment/add")
    public String addComment(@ModelAttribute CommentSaveDto CommentSaveDto,
                             Authentication authentication) {
        commentService.addComment(CommentSaveDto, authentication);
        return "redirect:/detail/"+CommentSaveDto.getParentId();
    }
}
