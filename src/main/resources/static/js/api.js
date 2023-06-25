class API {
    static baseURL = "/";

    // 可选同步与异步执行
    // callback不为null或undefined
    // 将异步请求并执行callback
    // callback的第一个参数需预留
    // 给ajax返回的数据

    static getUser(uid, name, money, number, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/user/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "uid": uid,
                    "name": name,
                    "money": money,
                    "number": number
                }),
                timeout: 2000,
                dataType: "json",
                success: function(data) {
                    callback(data, args);
                },
                complete: function(XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            });
            return ;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/get",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "uid": uid,
                "name": name,
                "money": money,
                "number": number
            }),
            timeout: 2000,
            dataType: "json",
            success: function(data) {
                obj = data
            },
            complete: function(XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }

    static addUser(user) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "user": user
            }),
            timeout: 2000,
            dataType: "json",
            success: function(data) {
                obj = data;
            },
            complete: function(XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }

    static updateUser(user) {
        var obj;
        if (user.name === "") {
            user.name = null;
        }
        if (user.password === "") {
            user.password = null;
        }
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/update",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "user": user
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function(XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }

    static deleteUser(uid, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/user/delete",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "uid": uid
                }),
                timeout: 2000,
                dataType: "json",
                success: function(data) {
                    callback(data, args);
                },
                complete: function(XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            });
            return ;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/user/delete",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "uid": uid
            }),
            timeout: 2000,
            dataType: "json",
            success: function(data) {
                obj = data;
            },
            complete: function(XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }

    static getRecords(number, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/record/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "number": number
                }),
                timeout: 2000,
                dataType: "json",
                success: function(data) {
                    callback(data, args);
                },
                complete: function(XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            })
            return ;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/record/get",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "number": number
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function(XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }

    static addRecord(record) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/record/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "record": record
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function(XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }

    static getPrice(latest, number, callback, args) {
        if (callback !== null && callback !== undefined) {
            $.ajax({
                type: "POST",
                url: this.baseURL + "api/eprice/get",
                contentType: "application/json",
                data: JSON.stringify({
                    "token": $.cookie("token"),
                    "select": latest === true ? "latest" : "",
                    "number": number
                }),
                timeout: 2000,
                dataType: "json",
                success: function(data) {
                    callback(data, args);
                },
                complete: function(XMLHttpRequest, textStatus) {
                    if (textStatus === "success") {
                        return;
                    } else if (textStatus === "timeout") {
                        obj = {"code": -1, "message": "超时"};
                        callback(obj, args);
                    } else {
                        obj = {"code": -1, "message": "客户端错误"};
                        callback(obj, args);
                    }
                }
            })
            return ;
        }
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/eprice/get",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "select": latest === true ? "latest" : "",
                "number": number
            }),
            timeout: 2000,
            dataType: "json",
            success: function (data) {
                obj = data;
            },
            complete: function(XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }

    static addPrice(price) {
        var obj;
        $.ajax({
            type: "POST",
            url: this.baseURL + "api/eprice/add",
            async: false,
            contentType: "application/json",
            data: JSON.stringify({
                "token": $.cookie("token"),
                "eprice": price
            }),
            timeout: 2000,
            dataType: "json",
            success: function(data) {
                obj = data;
            },
            complete: function(XMLHttpRequest, textStatus) {
                if (textStatus === "success") {
                    return obj;
                } else if (textStatus === "timeout") {
                    obj = {"code": -1, "message": "超时"};
                } else {
                    obj = {"code": -1, "message": "客户端错误"};
                }
            }
        })
        return obj;
    }
}