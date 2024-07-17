package cn.bugstack.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @author wangyuefan
 * @date 2024/7/17 15:49
 * @describe 规则树实体对象
 */
@Data
public class RuleTree {

    /**
     * 自增ID
     */
    private Long id;
    /**
     * 规则树ID
     */
    private String treeId;
    /**
     * 规则树名称
     */
    private String treeName;
    /**
     * 规则树描述
     */
    private String treeDesc;
    /**
     * 规则根节点
     */
    private String treeRootRuleKey;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
