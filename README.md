# ahocorasick

基于`Scala`实现`Aho-Corasick`，根据中文特点修改默认实现，实现匹配最长关键词的功能，而且通过限制每个Trie节点的只能存贮一个关键词，有效的减少了存储空间，能支持上百万关键词的搜索匹配


# Example

例如： 关键词 `阜新市` 和 `新市`，假如匹配的字符串为`阜新市`，在原来的`Aho-Corasick`算法实现中会给出两个匹配关键词，但是其实我们想要的地点是最长的`阜新市`，通过我们修改算法默认实现能够实现只给出`阜新市`这个匹配项

    import me.zhanglun.ahocorasick

    val trie = new Trie().build().addKeyword("阜新市").addKeyword("新市").build()
    val word = trie.search("阜新市").toArray // word 为 阜新市


# Install


![](https://img.shields.io/badge/maven-v2.0.0-519dd9.svg)[Download](https://search.maven.org/artifact/me.zhanglun.ahocorasick/trie/)