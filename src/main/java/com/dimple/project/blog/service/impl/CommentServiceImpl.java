package com.dimple.project.blog.service.impl;

import com.dimple.common.utils.ObjectUtils;
import com.dimple.common.utils.SecurityUtils;
import com.dimple.project.blog.domain.Comment;
import com.dimple.project.blog.mapper.CommentMapper;
import com.dimple.project.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @className: CommentServiceImpl
 * @description:
 * @auther: Dimple
 * @date: 2019/10/26
 * @version: 1.0
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentMapper commentMapper;

    @Override
    public List<Comment> selectCommentList(Comment comment) {
        return commentMapper.selectCommentList(comment);
    }

    @Override
    public Comment selectCommentById(Long id) {
        return commentMapper.selectCommentById(id);
    }

    @Override
    public int insertComment(Comment comment) {
        comment.setAdminReply(SecurityUtils.isAdmin());
        return commentMapper.insertComment(comment);
    }

    @Override
    public int updateComment(Comment comment) {
        return commentMapper.updateComment(comment);
    }

    @Override
    public int deleteCommentById(Long id) {
        String username = SecurityUtils.getUsername();
        return commentMapper.deleteCommentById(id, username);
    }

    @Override
    public int incrementCommentGood(Long id) {
        return commentMapper.incrementCommentGood(id);
    }

    @Override
    public int incrementCommentBad(Long id) {
        return commentMapper.incrementCommentBad(id);
    }

    @Override
    public List<Comment> selectCommentListByPageId(Long id) {
        //查询获取所有的comment
        List<Comment> commentList = commentMapper.selectCommentListByPageId(id);
        List<Comment> result = commentList.stream().filter(e -> e.getParentId() == null).collect(Collectors.toList());
        //CommentId和NickName的映射Map
        Map<Long, String> commentIdAndNickNameMap = commentList.stream().collect(Collectors.toMap(Comment::getId, Comment::getNickName));
        for (Comment comment : result) {
            Long commentId = comment.getId();
            comment.setSubCommentList(commentList.stream().filter(e -> commentId.equals(e.getParentId())).collect(Collectors.toList()));
            //设置replyNickName
            if (ObjectUtils.isNotEmpty(comment.getSubCommentList())) {
                for (Comment temp : comment.getSubCommentList()) {
                    if (temp.getReplyId() != null) {
                        temp.setReplyNickName(commentIdAndNickNameMap.get(temp.getReplyId()));
                    }
                }
            }
        }
        return result;
    }

}
