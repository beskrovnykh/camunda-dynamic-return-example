package org.camunda.getstarted;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MainApplicationTest extends AbstractProcessEngineRuleTest {

  @Autowired
  private RuntimeService runtimeService;

  @Test
  @Deployment(resources = {"process.bpmn"})
  public void shouldExecuteHappyPath() {
    // given
    String processDefinitionKey = "unit-testing-process";

    // when
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);

    // then
    assertThat(runtimeService.createProcessInstanceQuery().count() == 1);
    assertThat(processInstance).isStarted()
        .task()
        .hasDefinitionKey("say-hello")
        .hasCandidateUser("admin")
        .isNotAssigned();

    // when we complete that task
    complete(task(processInstance));

    // then the process instance should be ended
    assertThat(processInstance).isEnded();
  }

}
