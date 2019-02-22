package com.std.biz;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.flowable.engine.DynamicBpmnService;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.idm.api.Group;
import org.flowable.idm.api.User;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.std.common.consts.CommonConsts.ValidState;
import com.std.persist.model.SysGroup;
import com.std.persist.model.SysManager;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:applicationContext-biz.xml"})
public class StdBizTest {
	private final static Logger logger = LoggerFactory.getLogger(StdBizTest.class);
    @Autowired
    private ISysService sysService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private FormService formService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private DynamicBpmnService dynamicBpmnService;
    
    /**
     * 用户新增修改时同步flowable用户表act_id_user
     * @param manager
     */
    private void syncFlowableUser(SysManager manager) {
		String userId = String.valueOf(manager.getId());
		identityService.deleteUser(userId);
		User flowableUser = identityService.newUser(userId);
		flowableUser.setEmail(manager.getMobile());
		flowableUser.setFirstName(manager.getName());
		flowableUser.setLastName(manager.getName());
		flowableUser.setDisplayName(manager.getAccount());
		flowableUser.setPassword("000000");
		identityService.saveUser(flowableUser);
    }
    
    /**
     * 部门新增修改时同步flowable组织表act_id_group
     * @param group
     */
    private void syncFlowableGroup(SysGroup group) {
		String groupId = String.valueOf(group.getId());
		identityService.deleteGroup(groupId);
		Group flowableGroup = identityService.newGroup(groupId);
		flowableGroup.setName(group.getName());
		flowableGroup.setType(group.getParentId().toString());
		identityService.saveGroup(flowableGroup);
    }
    
    /**
     * 绑定部门、人员关系
     * @param managerGroupState
     * @param managerId
     * @param groupId
     */
    private void syncFlowableMembership(String managerGroupState,String managerId,String groupId) {
    	if(ValidState.VALID.getValue().equals(managerGroupState)) {
    		// 删除绑定关系
    		identityService.deleteMembership(managerId, groupId);
    		// 重新绑定关系
    		identityService.createMembership(managerId, groupId);
    	}else {
    		// 删除绑定关系
    		identityService.deleteMembership(managerId, groupId);
    	}
    }
    
    /**
     * 发布请假单定义
     */
    private void deployHolidayBpmn(String resource) {
    	Deployment deployment = repositoryService.createDeployment()
    			  .addClasspathResource(resource)
    			  .deploy();
    	logger.info("Deployment id : {}", deployment.getId());
    	ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
    	logger.info("processDefinition : {}", processDefinition.getId());
    }
    
    /**
     * 发起流程
     */
    private void startFlowableProcessInstance(String processDefinitionKey,Map<String, Object> variables) {
    	identityService.setAuthenticatedUserId((String)variables.get("initiator"));
    	ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, variables);
    	identityService.setAuthenticatedUserId(null);
        logger.info("##########流程开启##########");
        logger.info("流程实例ID:" + processInstance.getId());
        logger.info("流程定义ID:" + processInstance.getProcessDefinitionId());
    }
    
    /**
     * 查询任务
     */
    private void flowableTaskQuery(String candidateUser) {
    	List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(candidateUser).active().list();
    	logger.info("#####################################################################");
    	logger.info("User: {} have {} tasks:", candidateUser, tasks.size());
    	for (int i=0; i<tasks.size(); i++) {
    		
    		logger.info("流程单：{}，发起人：{} > ", tasks.get(i).getName(), runtimeService.getVariableInstances(tasks.get(i).getExecutionId()));
    	}
    }
    
    /**
     * 删除流程定义(删除key相同的所有不同版本的流程定义)
     */
    private void deleteProcessDefinitionByKey(String processDefinitionKey) {
		List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).list();
		// 遍历，获取每个流程定义的部署ID
		if (list != null && list.size() > 0) {
			for (ProcessDefinition pd : list) {
				// 获取部署ID
				String deploymentId = pd.getDeploymentId();
				// /*
				// * 不带级联的删除， 只能删除没有启动的流程，如果流程启动，就会抛出异常
				// */
				// processEngine.getRepositoryService().deleteDeployment(deploymentId);
				/**
				 * 级联删除 不管流程是否启动，都可以删除
				 */
				System.out.println("删除部署：" + deploymentId);
				repositoryService.deleteDeployment(deploymentId, true);
			}
		}
    }
    
    private void assigneeProcessTask(String candidateUser,boolean approved,boolean isExistNextAssignee,String nextAssignee) {
    	List<Task> tasks = taskService.createTaskQuery().taskCandidateUser(candidateUser).list();
    	logger.info("#####################################################################");
    	logger.info("Assignee : {}, have {} tasks:", candidateUser, tasks.size());
    	logger.info("待办任务列表：");
    	for (int i=0; i<tasks.size(); i++) {
    		logger.info("流程单：{}，发起人：{} > ", tasks.get(i).getName(), taskService.getVariables(tasks.get(i).getId()));
    	}
    	if(tasks.size()>0) {
    		Map<String, Object> variables = new HashMap<String, Object>();
    		Task task = tasks.get(0);
    		variables.put("approved", approved);
    		variables.put("isExist", isExistNextAssignee);
    		variables.put("assignee", nextAssignee);
    		taskService.complete(task.getId(), variables);
    	}
    }
    
    /**
     * 查询任务
     */
    private void flowableHistoryTaskQuery(String candidateUser) {
    	List<HistoricTaskInstance> historyTasks = historyService.createHistoricTaskInstanceQuery().finished().taskCandidateUser(candidateUser).list();
    	logger.info("#####################################################################");
    	for (int i=0; i<historyTasks.size(); i++) {
    		HistoricTaskInstance ht = historyTasks.get(i);
    		List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery().processInstanceId(ht.getProcessInstanceId()).list();
    		logger.info("流程单：{}", ht.getName());
    		for(HistoricVariableInstance v : variables) {
    			logger.info("#DETAIL##########{}", v.getValue());
    		}
    	}
    }
    
    
    
    @Test
	public void test() {
    	String processDefinitionKey = "holidayRequest";
    	String resource = "flowable/bpmn20/holiday-request.bpmn20.xml";
    	
//    	deleteProcessDefinitionByKey(processDefinitionKey);
    	deployHolidayBpmn(resource);
    	
    	Map<String, Object> variables = new HashMap<String, Object>();
    	String [] assignees = {"2","1"};
    	variables.put("assignee", Arrays.asList(assignees));
    	variables.put("days", 7);
    	variables.put("description", "掉进了小河");
    	variables.put("initiator", "2");
//    	startFlowableProcessInstance(processDefinitionKey, variables);
    	
//    	flowableTaskQuery("4");
    	
    	String candidateUser = "1";
    	boolean approved = true;
    	boolean isExistNextAssignee = false;
    	String nextAssignee = "4";
//    	assigneeProcessTask(candidateUser, approved, isExistNextAssignee, nextAssignee);
    	
//    	flowableHistoryTaskQuery(candidateUser);
	}
    
    @Test
    @Ignore
    public void testFlowableTaskQuery() {
    	flowableTaskQuery("4");
    }
    
    @Test
    @Ignore
    public void testStartFlowableProcessInstance() {
    	String processDefinitionKey = "holidayRequest";
    	Map<String, Object> variables = new HashMap<String, Object>();
    	String [] assignees = {"2","1"};
    	variables.put("assignee", Arrays.asList(assignees));
    	variables.put("days", 7);
    	variables.put("description", "掉进了小河");
    	variables.put("initiator", "2");
    	startFlowableProcessInstance(processDefinitionKey, variables);
    }
    
    @Test
    @Ignore
    public void testDeployFlowable() {
    	String resource = "flowable/bpmn20/holiday-request.bpmn20.xml";
    	deployHolidayBpmn(resource);
    }
    
	@Test
	@Ignore
	public void testSyncFlowableUser() {
		// 用户新增修改时同步flowable用户表act_id_user
		SysManager manager = sysService.findSysManagerByAccount("admin");
		syncFlowableUser(manager);
		User queryUser = identityService.createUserQuery().userId(String.valueOf(manager.getId())).singleResult();
		logger.info("{}",queryUser.getDisplayName());
	}
	
	@Test
	@Ignore
	public void testSyncFlowableGroup() {
		// 部门新增修改时同步flowable组织表act_id_group
		SysGroup group = sysService.findSysGroupByCode("0000");
		syncFlowableGroup(group);
		Group queryGroup = identityService.createGroupQuery().groupId(String.valueOf(group.getId())).singleResult();
		logger.info("{}",queryGroup.getName());
	}

	@Test
	@Ignore
	public void testFlowableMembership() {
		SysManager manager = sysService.findSysManagerByAccount("admin");
		SysGroup group = sysService.findSysGroupByCode("0000");
		// 绑定部门、人员关系
		String userId = String.valueOf(manager.getId());
		String groupId = String.valueOf(group.getId());
		syncFlowableMembership(ValidState.VALID.getValue(), userId, groupId);
	}
}
