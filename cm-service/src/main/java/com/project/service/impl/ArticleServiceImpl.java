package com.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.project.VO.ArticleVO;
import com.project.VO.UserInfoVO;
import com.project.VO.UserVO;
import com.project.domain.Article;
import com.project.domain.Follows;
import com.project.domain.User;
import com.project.exception.BusinessExceptionHandler;
import com.project.mapper.ArticleMapper;
import com.project.mapper.FollowsMapper;
import com.project.mapper.UserInfoMapper;
import com.project.service.ArticleService;
import com.project.util.ThrowUtil;
import com.project.util.TokenUtil;
import com.project.util.UploadAvatar;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private FollowsMapper followsMapper;

    /**
     * 用户id
     */
    private Long userId;

    private final Gson gson = new Gson();

    /**
     * 发布动态
     *
     * @param token 用户id和手机号
     * @param text  动态文字
     * @param files 动态图片
     * @return success or fail
     */
    @Override
    public boolean publish(String token, String text, List<MultipartFile> files) {
        // 1. 解析token
        getUserId(token);

        // 2. 文字和图片不能同时为空
        if (text == null && files == null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数不能同时为空"));
        }

        Article article = new Article();
        article.setUserId(userId);
        if (text != null && !text.isEmpty()) {
            article.setArticleContent(text);
        }

        // 3. 图片上传到阿里云，然后获取新的访问地址
        List<String> links = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            files.forEach(file -> {
                try {
                    String newLink = UploadAvatar.uploadAvatar(file, "article");
                    links.add(newLink);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            String photos = gson.toJson(links);
            article.setArticlePhotos(photos);
        }

        return articleMapper.insert(article) == 1;
    }

    /**
     * 删除动态
     *
     * @param token 用户id和手机号
     * @return true or false
     */
    @Override
    public boolean delete(String token, Long articleId) {
        // 1. 解析token
        getUserId(token);

        // 2. 校验 articleId
        if (articleId == null || articleId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 查看删除的动态是否存在
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("article_id", articleId);
        Article article = articleMapper.selectOne(queryWrapper);
        if (article == null) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "没权限"));
        }

        // 4. 删除动态
        return articleMapper.deleteById(articleId) == 1;
    }

    /**
     * 查询自己发表的动态
     *
     * @param token   用户id和手机号
     * @return 动态数组
     */
    @Override
    public List<Article> queryAll(String token) {
        // 1. 解析token
        getUserId(token);

        // 2. 查询自己发表的动态
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return articleMapper.selectList(queryWrapper);
    }

    /**
     * 查询其他人的动态
     *
     * @param token   用户id和手机号
     * @return 动态数组
     */
    @Override
    public Map<String, Object> queryOtherAll(String token) {
        // 1. 解析token
        getUserId(token);

        // 2. 查询自己发表的动态
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("user_id", userId);
        List<Article> articles = articleMapper.selectList(queryWrapper);

        // 3. 获取 user_id
        Set<Long> userIds = new HashSet<>();
        articles.forEach(article -> userIds.add(article.getUserId()));

        // 4. 根据 user_id 查询用户信息
        List<User> users = userInfoMapper.selectBatchIds(userIds);

        // 5. 根据 user_id 查询用户是否被关注
        Map<Long, String> followMap = new HashMap<>();
        userIds.forEach(otherUserId -> {
            QueryWrapper<Follows> followsQueryWrapper = new QueryWrapper<>();
            followsQueryWrapper.eq("follower_id", userId)
                            .eq("followee_id", otherUserId);
            Follows one = followsMapper.selectOne(followsQueryWrapper);
            followMap.put(otherUserId, one == null ? "关注" : "已关注");
        });


        // 5. 返回数据
        Map<String, Object> map = new HashMap<>();
        map.put("users", users);
        map.put("articles", articles);
        map.put("followMap", followMap);
        return map;
    }

    /**
     * 模糊查询动态
     *
     * @param token   用户id和手机号
     * @param content 关键字
     * @return 动态集合
     */
    @Override
    public List<ArticleVO> queryArticle(String token, String content) {
        // 1. 解析token
        getUserId(token);

        // 2. 校验 content
        if (Objects.equals(content, "")) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 3. 构造查询条件
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("user_id", userId)
                .like("article_content", content);
        List<Article> articles = articleMapper.selectList(queryWrapper);

        // 4. 数据脱敏
        List<ArticleVO> list = new ArrayList<>();
        articles.forEach(article -> {
            ArticleVO articleVO = new ArticleVO();
            articleVO.setArticleId(article.getArticleId());
            articleVO.setArticleContent(article.getArticleContent());
            articleVO.setCreateTime(article.getCreateTime());
            // 根据用户id查询出用户名
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<User>();
            queryWrapper1.eq("user_id", article.getUserId());
            User user = userInfoMapper.selectOne(queryWrapper1);
            articleVO.setUserName(user.getUserName());
            list.add(articleVO);
        });

        return list;
    }

    /**
     * 根据用户id查询动态
     *
     * @param userId 用户id
     * @return 动态集合
     */
    @Override
    public List<Article> queryArticleByUserId(Long userId) {
        // 1. 校验用户id
        if (userId == null || userId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 2. 构造查询条件
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return articleMapper.selectList(queryWrapper);
    }

    /**
     * 根据文章id查询动态
     *
     * @param articleId
     * @return
     */
    @Override
    public List<Object> queryArticleByArticleId(Long articleId) {
        // 1. 校验articleId
        if (articleId == null || articleId <= 0) {
            ThrowUtil.throwByObject(new BusinessExceptionHandler(401, "参数错误"));
        }

        // 2. 构造查询条件
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);
        Article article = articleMapper.selectOne(queryWrapper);

        // 3. 根据userId查询出用户id、用户头像、用户名称
        Long id = article.getUserId();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_id", id);
        User user = userInfoMapper.selectOne(userQueryWrapper);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 4. 封装数据
        List<Object> list = new ArrayList<>();
        list.add(article);
        list.add(userVO);
        return list;
    }

    /**
     * 解析token
     *
     * @param token 用户id和手机号
     */
    private void getUserId(String token) {
        Map<String, Object> stringObjectMap = TokenUtil.parseToken(token);
        userId = (Long) stringObjectMap.get("userId");
    }
}
