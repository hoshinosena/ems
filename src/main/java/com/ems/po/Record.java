package com.ems.po;

public class Record {
    // 主键 自增
    private int rid;
    /*
    操作属性(op)  操作内容(change) API上传内容
    0: 管理员修改 余额            余额
    1: 用户充值   金额            金额
    2: 用户消费   金额            电量(服务器自动换算金额)
     */
    private int op;
    private double changes;
    private String date;
    private String info;
    // 外键 当期电价 1:1
    private int pid;
    // 外键 所属用户 1:1
    private int uid;

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public double getChanges() {
        return changes;
    }

    public void setChanges(double changes) {
        this.changes = changes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
