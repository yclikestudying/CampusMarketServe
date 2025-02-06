package com.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.VO.ArticleVO;
import com.project.domain.Article;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ArticleService extends IService<Article> {
    // 发布动态
    boolean publish(String token, String text, MultipartFile file, Integer count, String textContent);

    // 删除动态
    boolean delete(String token, Long articleId);

    // 查询自己发表的动态
    List<Article> queryAll(String token);

    // 查询其他人的动态
    Map<String, Object> queryOtherAll(String token);

    // 模糊查询动态
    List<ArticleVO> queryArticle(String token, String content);

    // 根据用户id查询动态
    List<Article> queryArticleByUserId(Long userId);

    // 根据文章id查询动态
    List<Object> queryArticleByArticleId(Long articleId);

}
