package org.bitbucket.eunjeon.seunjeon;

import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;


public class ReadmeJavaTest {

    @Test
    public void lexiconDictByUser() {
        Iterator<String> itr = Arrays.asList("할리스커피", "포메인,-100", "김밥+천국,-100").iterator();
        LexiconDict dict = new LexiconDict();
        // scala.collection.

        dict.loadFromIterator(scala.collection.JavaConverters.asScalaIterator(itr), true);
    }
    @Test
    public void test_UerDict() {
        String sentence = "덕후냄새가 난다.";
        System.out.println("[BEFORE]");
        for (LNode node : CompressedAnalyzer.parseJava(sentence)) {
            System.out.println(node);
        }
        /**
         * 사용자 사전 추가
         * surface,cost
         *   surface: 단어명. '+' 로 복합명사를 구성할 수 있다.
         *           '+'문자 자체를 사전에 등록하기 위해서는 '\+'로 입력. 예를 들어 'C\+\+'
         *   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
         */
        Iterator<String> itr = Arrays.asList("덕후", "버카충,-100", "낄끼+빠빠,-100").iterator();
        CompressedAnalyzer.setUserDict(itr);
        System.out.println("[AFTER]");
        for (LNode node : CompressedAnalyzer.parseJava(sentence)) {
            System.out.println(node);
        }

        itr= Arrays.asList("할리스", "한솥").iterator();
        CompressedAnalyzer.setUserDict(itr);
        Iterable<LNode> nodes = CompressedAnalyzer.parseJava(sentence);
        for (LNode node : nodes) {
            System.out.println(node);
        }
    }

    @Test
    public void test_dictionary_per_user() {
//        LexiconDict dict = new LexiconDict();
//        dict.loadFromString(false, "덕후", "버카충,-100", "낄끼+빠빠,-100");
        CompressedAnalyzer.parseJava("안녕");

        String s = "안동시체육회";
        Iterable<LNode> nodes = CompressedAnalyzer.parseJava(s);
        System.out.println("[BEFORE]");
        for(LNode node: nodes) {
            System.out.println(node);
        }

        LexiconDict d0 = new LexiconDict();
        d0.loadFromString(false, "안동,-3000", "육회,-4000");
        nodes = CompressedAnalyzer.parseJava(s, d0);
        System.out.println("[WITH USER DICT]");
        for(LNode node: nodes) {
            System.out.println(node);
        }

        LexiconDict d1 = new LexiconDict();
        d1.loadFromString(false, "동시,-3000");
        System.out.println("[WITH USER DICT]");
        printNodes(s, d1);
    }
    @Test
    public void test_엄마가죽() {
        String s= "엄마가죽을남겨두셨다";
        LexiconDict d = new LexiconDict();
        d.loadFromString(false,  "가죽,-10000");
        printNodes(s, d);
    }
    @Test
    public void test_불안감() {
        String s= "뭔지모를이불안감의정체";
        LexiconDict d = new LexiconDict();
        d.loadFromString(false,  "가죽,-10000");
        printNodes(s, d);
    }
    @Test
    public void test_유아인() {
        String s = "한국유아인성교육소";
        LexiconDict d = new LexiconDict();
        d.loadFromString(false, "유아인,-1000", "성교,-1000");
        Iterable<LNode> nodes = CompressedAnalyzer.parseJava(s, d);
        for (LNode node : nodes) {
            System.out.println(node);
        }
        d.loadFromString(false, "한국유,-1000", "아인성,-1000");
        printNodes(s, d);

        d.loadFromString(false, "유아인,-1000", "성교,-1000");
        printNodes(s, d);
    }
    @Test
    public void test_서울시장_애인() {
        String s = "서울시장애인복지관";
        printNodes(s, null);

        LexiconDict d = new LexiconDict();
        d.loadFromString(false, "시장,-1000");
        printNodes(s, d);

        d = new LexiconDict();
        d.loadFromString(false, "장애인,-2000");
        printNodes(s, d);

    }

    @Test
    public void test_안주다() {
        CompressedAnalyzer.parseJava("안녕");
        // 안달리다,
        String s = "소녀시대유감입니다.";
        LexiconDict dic = new LexiconDict();
        dic.loadFromString(true, "소녀+시대,-1000");
        Iterable<LNode> nodes = CompressedAnalyzer.parseJava(s, dic);
        for(LNode node: nodes) {
            Morpheme m = node.morpheme();
            System.out.printf("%s: %s\n", m.getSurface(), m.getFeature());
        }

        // dic = new LexiconDict();
        dic.loadFromString(true, "시대+유감,-1000");
        nodes = CompressedAnalyzer.parseJava(s, dic);
        for(LNode node: nodes) {
            Morpheme m = node.morpheme();
            System.out.printf("%s: %s\n", m.getSurface(), m.getFeature());
        }

    }

    private void printNodes(String s, LexiconDict d) {
        Iterable<LNode> nodes;
        nodes = CompressedAnalyzer.parseJava(s, d);
        for (LNode node : nodes) {
            System.out.println(node);
        }
    }

    @Test
    public void testReadme() {
        // 형태소 분석
        for (LNode node : Analyzer.parseJava("아버지가방에들어가신다.")) {
            System.out.println(node);
        }

        // 어절 분석
        for (Eojeol eojeol: Analyzer.parseEojeolJava("아버지가방에들어가신다.")) {
            System.out.println(eojeol);
            for (LNode node: eojeol.nodesJava()) {
                System.out.println(node);
            }
        }

        /**
         * 사용자 사전 추가
         * surface,cost
         *   surface: 단어명. '+' 로 복합명사를 구성할 수 있다.
         *           '+'문자 자체를 사전에 등록하기 위해서는 '\+'로 입력. 예를 들어 'C\+\+'
         *   cost: 단어 출연 비용. 작을수록 출연할 확률이 높다.
         */
        Analyzer.setUserDict(Arrays.asList("덕후", "버카충,-100", "낄끼+빠빠,-100").iterator());
        for (LNode node : Analyzer.parseJava("덕후냄새가 난다.")) {
            System.out.println(node);
        }

        // 활용어 원형
        for (LNode node : Analyzer.parseJava("빨라짐")) {
            for (LNode node2: node.deInflectJava()) {
                System.out.println(node2);
            }
        }

        // 복합명사 분해
        for (LNode node : Analyzer.parseJava("낄끼빠빠")) {
            System.out.println(node);   // 낄끼빠빠
            for (LNode node2: node.deCompoundJava()) {
                System.out.println(node2);  // 낄끼+빠빠
            }
        }

        // 압축모드 분석(heap memory 사용 최소화. 속도는 상대적으로 느림. -Xmx512m 이하 추천)
        for (LNode node : CompressedAnalyzer.parseJava("아버지가방에들어가신다.")) {
            System.out.println(node);
        }
    }
}
