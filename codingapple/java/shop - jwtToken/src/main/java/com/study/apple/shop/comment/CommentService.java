package com.study.apple.shop.comment;

import com.study.apple.shop.member.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void addComment(CommentSaveDto commentSaveDto, Authentication authentication) {
        CustomUser customUser = (CustomUser) authentication.getPrincipal();
        Comment comment = Comment.builder()
                .content(commentSaveDto.getContent())
                .parentId(commentSaveDto.getParentId())
                .username(customUser.getDisplayName())
                .build();

        commentRepository.save(comment);
    }

    public List<CommentSaveDto> getComment(Long parentId) {
        return commentRepository.findAllByParentIdOrderByIdDesc(parentId)
                .stream().map(Comment ->{
                    CommentSaveDto commentSaveDto = new CommentSaveDto();
                    commentSaveDto.setContent(Comment.getContent());
                    commentSaveDto.setParentId(Comment.getParentId());
                    commentSaveDto.setUsername(Comment.getUsername());
                    return commentSaveDto;
                }).toList();
    }

}
