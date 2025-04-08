package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search" , method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model){
        List<DiscussPost> list = elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if (list != null){
            for (DiscussPost post : list) {
                Map<String,Object> map = new HashMap<>();
                // 帖子
                map.put("post",post);
                // 作者
                map.put("user",userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount",likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);
        page.setPath("/search?keyword=" + keyword);
        page.setRows(list == null ? 0 : list.size());
        return "/site/search";
    }

}
