package me.zhanglun.ahocorasick

import org.junit.{Assert, Test}

@Test
class TrieTest extends Assert{
  @Test
  def testMatchLonest: Unit = {
    val trie = new Trie().build().addKeyword("北京市").addKeyword("京市").build()
    val word = trie.search("北京市").toArray
    assert(word.length == 1)

    assert(word(0).keyword == "北京市")
  }

}
