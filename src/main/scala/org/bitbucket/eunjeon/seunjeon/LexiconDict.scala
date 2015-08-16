package org.bitbucket.eunjeon.seunjeon

import java.io.{File, _}

import com.google.common.collect.ImmutableList

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.io.Source
import scala.util.control.NonFatal
import scala.util.matching.Regex

object Term {
  def createUnknownTerm(surface:String): Term = {
    new Term(surface, -1, -1, 4000*surface.length, "UNKNOWN")
  }
}

case class Term(surface:String,
                leftId:Short,
                rightId:Short,
                cost:Int,
                feature:String) {
}

object LexiconDict {
  val lexiconResourceFile = "/lexicon.dat"
  val lexiconTrieResourceFile = "/lexicon_trie.dat"
}

class LexiconDict {
  var surfaceIndexDict: ImmutableList[(String, ImmutableList[Term])] = null
  var trie: DoubleArrayTrie = null

  def loadFromCsvFiles(dir: String): Unit = {
    val r = new Regex(".+[.]csv")
    val files = new File(dir).listFiles.filter(f => r.findFirstIn(f.getName).isDefined)
    val totalIter:Iterator[String] = files.map(f => Source.fromFile(f, "utf-8").getLines()).reduceLeft(_ ++ _)
    loadFromIterator(totalIter)
  }

  def loadFromString(str: String): Unit = {
    val iterator = str.stripMargin.split("\n").toIterator
    loadFromIterator(iterator)
  }

  def loadFromIterator(iterator: Iterator[String]): Unit = {
    // TODO: Option 사용해보자.
    val terms = new mutable.MutableList[Term]()
    iterator.foreach { line =>
      try {
        val l = line.split(",")
        // TODO: 사전에 이상한 문자가 있어서 trie이가 이상하게 돌아가는 듯.. 원인일 찾아봐야 함.
        // 그래서 "흐라" 검색하니 "헬렌켈러"가 나옴.
        if (! l(0).exists(ch => ch < '가'|| '힣' < ch)) {
          terms += Term(l(0), l(1).toShort, l(2).toShort, l(3).toShort, l.slice(4, l.size - 1).mkString(","))
        }
      } catch {
        case NonFatal(exc) => println(exc)
      }
    }
    build(terms.toIndexedSeq.sortBy(_.surface))
  }

  private def build(terms: Seq[Term]): Unit = {
    val surfaceIndexDictTemp = terms.groupBy(t => t.surface).toIndexedSeq.sortBy(t => t._1)
    val surfaceIndexDictTemp2 = surfaceIndexDictTemp.map{it =>
      val surface = it._1
      val terms = ImmutableList.copyOf[Term](it._2)
      (surface, terms)
    }
    surfaceIndexDict = ImmutableList.copyOf[(String, ImmutableList[Term])](surfaceIndexDictTemp2)

    trie = new DoubleArrayTrie
    trie.build(surfaceIndexDict.map(t => t._1))
  }

  def prefixSearch(keyword: String): Seq[Term] = {
    val indexedLexiconDictPositions = trie.commonPrefixSearch(keyword)
    indexedLexiconDictPositions.flatMap { indexLexiconDictPos =>
      surfaceIndexDict.get(indexLexiconDictPos)._2
    }
  }

  def save(lexiconPath: String = LexiconDict.lexiconResourceFile,
           lexiconTriePath: String = LexiconDict.lexiconTrieResourceFile): Unit = {
    // TODO: Term 에서 surface 를 빼면 serialize deserialze하는데 더 빠를 것 같음.
    val store = new ObjectOutputStream(
      new BufferedOutputStream(
        new FileOutputStream(lexiconPath), 1024*16))
    store.writeObject(surfaceIndexDict)
    store.close()
    trie.save(lexiconTriePath)
  }

  def load(): LexiconDict = {
    val lexiconStream = getClass.getResourceAsStream(LexiconDict.lexiconResourceFile)
    val lexiconTrieStream = getClass.getResourceAsStream(LexiconDict.lexiconTrieResourceFile)

    load(lexiconStream, lexiconTrieStream)
    this
  }

  private def load(lexiconStream: InputStream, lexiconTrieStream: InputStream): Unit = {
    val in = new ObjectInputStream(
      new BufferedInputStream(lexiconStream, 1024*16))
    surfaceIndexDict = in.readObject().asInstanceOf[ImmutableList[(String, ImmutableList[Term])]]
    in.close()

    trie = new DoubleArrayTrie
    trie.open(lexiconTrieStream)

  }
}
