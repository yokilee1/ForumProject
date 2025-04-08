package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost; // 假设DiscussPost为实体类
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;

import java.io.Serializable;
import java.util.*;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {

    @Autowired
    DiscussPostRepository esTestRepository;

    @Autowired
    ElasticsearchRestTemplate Template;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void testInsert() {
        esTestRepository.save(discussPostMapper.selectDiscussPostById(241));
        esTestRepository.save(discussPostMapper.selectDiscussPostById(242));
    }

    @Test
    public void teatInsertList() {
        esTestRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        esTestRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        esTestRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        esTestRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        esTestRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
        esTestRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
        esTestRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
        esTestRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
        esTestRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水。");
        esTestRepository.save(post);
    }

    @Test
    public void testDelete() {
        esTestRepository.deleteById(231);
    }

    // 测试搜索功能
    @Test
    public void testSearchByRepository() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                       new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> searchHits = Template.search(searchQuery, DiscussPost.class);

        List<DiscussPost> results = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            DiscussPost post = hit.getContent();

            // 提取高亮
            Map<String, List<String>> highlights = hit.getHighlightFields();
            if (highlights.containsKey("title")) {
                post.setTitle(highlights.get("title").get(0));
            }
            if (highlights.containsKey("content")) {
                post.setContent(highlights.get("content").get(0));
            }

            results.add(post);
        }

        // 输出
        System.out.println("共找到：" + searchHits.getTotalHits() + " 条结果");
        results.forEach(System.out::println);
    }


    @Test
    public void testSearchByTemplate() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                )
                .build();

        SearchHits<DiscussPost> searchHits = Template.search(searchQuery, DiscussPost.class);

        List<DiscussPost> results = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            DiscussPost post = hit.getContent();

            // 高亮处理
            Map<String, List<String>> highlights = hit.getHighlightFields();
            if (highlights.containsKey("title")) {
                post.setTitle(highlights.get("title").get(0));
            }
            if (highlights.containsKey("content")) {
                post.setContent(highlights.get("content").get(0));
            }

            results.add(post);
        }

        System.out.println("总记录数: " + searchHits.getTotalHits());
        System.out.println("当前页：" + searchHits.getSearchHits());
        System.out.println("当前页数量：" + searchHits.getSearchHits().size());

        results.forEach(System.out::println);
    }



}
