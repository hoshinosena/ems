class Utils{

    static sleep(time) {
        var time_ms = new Date().getTime();
        while (new Date().getTime() < time_ms + time) {}
    }

    static getTime(fmt, timestamp) {
        var o;
        var time = new Date();
        if (timestamp === undefined || timestamp === null) {
            o = {
                "M+" : time.getMonth()+1,                 //月份
                "d+" : time.getDate(),                    //日
                "h+" : time.getHours(),                   //小时
                "m+" : time.getMinutes(),                 //分
                "s+" : time.getSeconds(),                 //秒
                "q+" : Math.floor((time.getMonth()+3) / 3), //季度
                "S" : time.getMilliseconds()             //毫秒
            };
        } else {
            var ms, s, m, h, d, M;
            ms = timestamp % 1000;
            s  = Math.floor(timestamp / 1000);
            m  = Math.floor(s / 60);
            h  = Math.floor(m / 60);
            d  = Math.floor(h / 24);
            M  = Math.floor(d / 30);
            o = {
                "M+" : M % 12,                 //月份
                "d+" : d % 30,                 //日
                "h+" : h % 24,                 //小时
                "m+" : m % 60,                 //分
                "s+" : s % 60,                 //秒
                "q+" : Math.floor((M + 3) / 3),            //季度
                "S" : ms                       //毫秒
            };
        }
        if(/(y+)/.test(fmt)) {
            fmt=fmt.replace(RegExp.$1, (time.getFullYear()+"").substr(4 - RegExp.$1.length));
        }
        for(var k in o) {
            if(new RegExp("("+ k +")").test(fmt)){
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
            }
        }
        return fmt;
    }
}

class RandomUtil {

    static randomIntRange(min, max) {
        return parseInt(Math.random() * (max - min) + min, 10);
    }

    static randomInt(n) {
        return this.randomIntRange(0, n);
    }

    static randomSeq(count) {
        let str = '';
        for (let i = 0; i < count; ++i) {
            str += seq[this.randomInt(62)];
        }
        return str;
    }

    static randomLowerAndNum(count) {
        let str = '';
        for (let i = 0; i < count; ++i) {
            str += seq[this.randomInt(36)];
        }
        return str;
    }

    static randomMTSecret() {
        let str = '';
        for (let i = 0; i < 32; ++i) {
            let index = this.randomInt(16);
            if (index <= 9) {
                str += index;
            } else {
                str += seq[index - 10];
            }
        }
        return str;
    }

    static randomUUID() {
        let d = new Date().getTime();
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            let r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c === 'x' ? r : (r & 0x7 | 0x8)).toString(16);
        });
    }
}