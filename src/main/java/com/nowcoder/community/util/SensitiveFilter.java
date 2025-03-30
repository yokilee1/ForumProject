package com.nowcoder.community.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private  class TrieNode{
        // 是否为关键词的结尾
        private boolean isKeywordEnd = false;

        // 子节点
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        // 判断是否为关键词的结尾
        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        // 设置是否为关键词的结尾
        public void setKeywordEnd(boolean isKeywordEnd) {
            this.isKeywordEnd = isKeywordEnd;
        }

        // 添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        // 获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }



    }

}
