package com.ll.exam.sbb;

import com.ll.exam.sbb.answer.Answer;
import com.ll.exam.sbb.answer.AnswerRepository;
import com.ll.exam.sbb.question.Question;
import com.ll.exam.sbb.question.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AnswerRepositoryTests {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;

    @BeforeEach
    void beforeEach() {
        clearData();
        createSampleData();
    }

    private void clearData() {
        QuestionRepositoryTests.clearData(questionRepository);

        answerRepository.deleteAll(); // DELETE FROM question;
        answerRepository.truncateTable();
    }

    private void createSampleData() {
        QuestionRepositoryTests.createSampleData(questionRepository);

        // 관련 답변이 하나없는 상태에서 쿼리 발생
        Question q = questionRepository.findById(1).get();

        System.out.println("q 1st : " + q);

        Answer a1 = new Answer();
        a1.setContent("sbb는 질문답변 게시판 입니다.");
        a1.setCreateDate(LocalDateTime.now());
        q.addAnswer(a1);

        Answer a2 = new Answer();
        a2.setContent("sbb에서는 주로 스프링부트관련 내용을 다룹니다.");
        a2.setCreateDate(LocalDateTime.now());
        q.addAnswer(a2);

        questionRepository.save(q);
    }

    @Test
    @Transactional
    @Rollback(false)
    void save() {
        Question q = questionRepository.findById(2).get();

        Answer a1 = new Answer();
        a1.setContent("네 자동으로 생성됩니다.");
        a1.setCreateDate(LocalDateTime.now());
        q.addAnswer(a1);

        Answer a2 = new Answer();
        a2.setContent("네네~ 맞아요!");
        a2.setCreateDate(LocalDateTime.now());
        q.addAnswer(a2);

        questionRepository.save(q);
    }

    @Test
    @Transactional
    @Rollback(false)
    void 조회() {
        Answer a = this.answerRepository.findById(1).get();
        assertThat(a.getContent()).isEqualTo("sbb는 질문답변 게시판 입니다.");
    }

    @Test
    @Transactional
    @Rollback(false)
    void 관련된_question_조회() {
        Answer a = this.answerRepository.findById(1).get();
        Question q = a.getQuestion();

        assertThat(q.getId()).isEqualTo(1);
    }

    @Test
    @Transactional
    @Rollback(false)
    void question으로부터_관련된_질문들_조회() {
        Question q = questionRepository.findById(1).get();

        List<Answer> answerList = q.getAnswerList();

        assertThat(answerList.size()).isEqualTo(2);
        assertThat(answerList.get(0).getContent()).isEqualTo("sbb는 질문답변 게시판 입니다.");
    }
}
