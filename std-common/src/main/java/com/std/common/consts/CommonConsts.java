package com.std.common.consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.ConfigurableConstants;

public class CommonConsts extends ConfigurableConstants {
	private final static Logger logger = LoggerFactory.getLogger(CommonConsts.class);
    static {
    	logger.info("load std common.properties");
        init("common.properties");
    }
    public static final String ENCRYPT_AES_CKEY = getValue("common.encrypt.aes.ckey");
	/**
	 * 处理结果 success or fail
	 * @author liuwei3
	 *
	 */
	public enum ProcessResult {
		SUCCESS("success"),//成功
		FAIL("fail");//失败
    	
		private String value;
		
		private ProcessResult(String value){
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	/**
	 * 有效无效状态
	 * @author liuwei3
	 */
    public enum ValidState {
    	VALID("1"),//有效
    	INVALID("0");//无效
    	
		private String value;
		
		private ValidState(String value){
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static boolean isExistValue(String value) {
			boolean isExist = false;
            for (ValidState vs : ValidState.values()) {
                if (vs.getValue().equals(value)) {
                    isExist = true;
                    break;
                }
            }
			return isExist;
		}
    }
    
    /**
     * 权限条目类别
     * @author liuwei3
     *
     */
    public enum PermissionType {
    	DIRECTORY("0"),//目录
    	MENU("1"),//菜单
    	BUTTON("2");//按钮
    	
		private String value;
		
		private PermissionType(String value){
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		public static boolean isExistValue(String value) {
			boolean isExist = false;
            for (PermissionType pt : PermissionType.values()) {
                if (pt.getValue().equals(value)) {
                    isExist = true;
                    break;
                }
            }
			return isExist;
		}
    }
    /**
     * 长度限制常量
     * @author liuwei3
     */
    public enum LengthLimit {
    	ACCOUNT(5,20),//账号
    	NAME(0,20),//姓名
    	PWD(6,12),//密码
    	ROLE_NAME(5,20),//角色名
    	ROLE_DESCRIPTION(0,20),//角色描述
    	PERMISSION_MODULE_NAME(1,12),//权限对应模块名
    	PERMISSION_MODULE_TAG(0,20),//权限对应模块标记
    	PERMISSION_URL(0,500),//功能模块URL
    	PERMISSIONS(0,250),//权限字串
    	PERMISSION_CLASS_ICON(0,50),//菜单图标样式
    	GROUP_NAME(1,50),//组织名称
    	GROUP_CODE(1,50)//组织编码
    	;
    	
    	private long minLength;
    	private long maxLength;
    	
		private LengthLimit(long minLength,long maxLength){
			this.minLength = minLength;
			this.maxLength = maxLength;
		}
		
    	public long getMinLength() {
    		return minLength;
    	}
    	public long getMaxLength() {
    		return maxLength;
    	}
    }
    /**
     * 参考消息
     * @author liuwei3
     */
    public enum ReferenceMessage {
    	ACCOUNT_PWD_ERROR_TOO_FREQUENCY("E-10001","账号或密码错误过于频繁，请稍后再试！"),
    	ACCOUNT_PWD_ERROR("E-10002","账号或密码错误！"),
    	ACCOUNT_LOCK("E-10003","账号已被锁定,请联系管理员！"),
    	ACCOUNT_VERIFY_ERROR("E-10004","账号规则验证不通过！"),
    	PWD_VERIFY_ERROR("E-10005","密码规则验证不通过！"),
    	MOBILE_VERIFY_ERROR("E-10006","手机号规则验证不通过！"),
    	STATE_VERIFY_ERROR("E-10007","状态规则验证不通过！"),
    	SAVE_SUCCESS("E-10008","保存成功！"),
    	SAVE_SUCCESS_BUT_NO_CHANGE("E-10009","保存成功,但数据没有发生变化！"),
    	SAVE_FAIL("E-10010","保存失败！"),
    	ACCOUNT_EXISTS("E-10011","账号已经存在！"),
    	ROLE_NAME_VERIFY_ERROR("E-10012","角色名规则验证不通过！"),
    	UPDATE_NOT_ALLOWED("E-10013","修改不被允许！"),
    	ROLE_EXISTS("E-10014","角色已经存在！"),
    	ROLE_DESCRIPTION_VERIFY_ERROR("E-10015","角色描述规则验证不通过！"),
    	PARAM_ERROR("E-10016","参数错误！"),
    	MODULE_NAME_VERIFY_ERROR("E-10017","功能名规则验证不通过！"),
    	MODULE_TAG_VERIFY_ERROR("E-10018","功能标记规则验证不通过！"),
    	URL_VERIFY_ERROR("E-10019","URL规则验证不通过！"),
    	PERMISSION_VERIFY_ERROR("E-10020","权限规则验证不通过！"),
    	CLASS_ICON_VERIFY_ERROR("E-10021","样式规则验证不通过！"),
    	ORDER_NUM_VERIFY_ERROR("E-10022","排序号规则验证不通过！"),
    	TYPE_VERIFY_ERROR("E-10023","类型规则验证不通过！"),
    	NAME_VERIFY_ERROR("E-10024","姓名规则验证不通过！"),
    	GROUP_NAME_ERROR("E-10025","组织名称验证不通过！"),
    	GROUP_CODE_ERROR("E-10026","组织编码验证不通过！"),
    	GROUP_CODE_EXISTS("E-10027","组织编码已经存在！"),
    	ACCOUNT_GROUP_EXISTS("E-10028","人员组织关系已经存在！"),
    	CAPTCHA_ERROR("E-10029","验证码错误！")
    	;
    	
		private String code;
    	private String message;
		
		private ReferenceMessage(String code,String message){
			this.code = code;
			this.message = message;
		}

		public String getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}
    }
}
