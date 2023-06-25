import random;

lastname = "王、李、张、刘、陈、杨、黄、赵、吴、周、徐、孙、马、朱、胡、郭、何、高、林、郑、谢罗、梁、宋、唐、许、韩、冯、邓、曹、彭、曾、萧、田、董、袁、潘、于、蒋、蔡、余、杜、叶、程、苏、魏、吕、丁、任、沈、姚、卢、姜、崔、钟、谭、陆、汪、范、金、石、廖、贾、夏、韦、付、方、白、邹、孟、熊、秦、邱、江、尹、薛、闫、段、雷、侯、龙、史、陶、黎、贺、顾、毛、郝、龚、邵、万、钱、严、覃、武、戴、莫、孔、向、汤".split("、")
firstname = ['伟','刚','勇','毅','俊','峰','强','军','平','保','东','文','辉','力','明','永','健','世','广','志','义','兴','良','海','山','仁','波','宁','贵','福','生','龙','元','全','国','胜','学','祥','才','发','武','新','利','清','飞','彬','富','顺','信','子','杰','涛','昌','成','康','星','光','天','达','安','岩','中','茂','进','林','有','坚','和','彪','博','诚','先','敬','震','振','壮','会','思','群','豪','心','邦','承','乐','绍','功','松','善','厚','庆','磊','民','友','裕','河','哲','江','超','浩','亮','政','谦','亨','奇','固','之','轮','翰','朗','伯','宏','言','若','鸣','朋','斌','梁','栋','维','启','克','伦','翔','旭','鹏','泽','晨','辰','士','以','建','家','致','树','炎','德','行','时','泰','盛','雄','琛','钧','冠','策','腾','楠','榕','风','航','弘','秀','娟','英','华','慧','巧','美','娜','静','淑','惠','珠','翠','雅','芝','玉','萍','红','娥','玲','芬','芳','燕','彩','春','菊','兰','凤','洁','梅','琳','素','云','莲','真','环','雪','荣','爱','妹','霞','香','月','莺','媛','艳','瑞','凡','佳','嘉','琼','勤','珍','贞','莉','桂','娣','叶','璧','璐','娅','琦','晶','妍','茜','秋','珊','莎','锦','黛','青','倩','婷','姣','婉','娴','瑾','颖','露','瑶','怡','婵','雁','蓓','纨','仪','荷','丹','蓉','眉','君','琴','蕊','薇','菁','梦','岚','苑','婕','馨','瑗','琰','韵','融','园','艺','咏','卿','聪','澜','纯','毓','悦','昭','冰','爽','琬','茗','羽','希','欣','飘','育','滢','馥','筠','柔','竹','霭','凝','晓','欢','霄','枫','芸','菲','寒','伊','亚','宜','可','姬','舒','影','荔','枝','丽','阳','妮','宝','贝','初','程','梵','罡','恒','鸿','桦','骅','剑','娇','纪','宽','苛','灵','玛','媚','琪','晴','容','睿','烁','堂','唯','威','韦','雯','苇','萱','阅','彦','宇','雨','洋','忠','宗','曼','紫','逸','贤','蝶','菡','绿','蓝','儿','翠','烟']
role = "user"
prefixusername = "test"
password = "test"
prefixtel = "133、153、189、180、130、132、145、186、139、147、150、188".split("、")
preaddress = "北京市海淀区翻斗大街翻斗花园"
info = "测试用数据"
lln = len(lastname)
lfn = len(firstname)
lt  = len(prefixtel)


num_user = 100
num_record = num_user * 100
num_eprice = num_user // 1000
random.seed(100)
file = open(r"./data.sql", "w")
file.write("use database ems;")
i, j, k = 0, 0, 0
for x in range(2, num_user + 1):
    j %= 31
    k %= 7
    i = x // 181 + 1
    j += 1
    k += 1
    name = lastname[random.randint(0, lln-1)] + firstname[random.randint(0, lfn-1)] + firstname[random.randint(0, lln-1)]
    money = 0
    if (random.randint(0, 1000) < 10):
        money = random.randint(-100, 0)
    else:
        money = random.randint(0, 1000)
    tel = str(prefixtel[random.randint(0, lt-1)]) + str(random.randint(10000000, 100000000))
    address = preaddress + str(i) + "号楼" + ((str(j) if (9 < j) else ("0" + str(j))) + "0" + str(k) + "室")
    file.write(f"insert into user(uid,role,username,password,name,money,tel,address,info) values({x},'{role}','{prefixusername + str(x)}','{password}','{name}',{money},'{tel}','{address}','{info}');\n")


for x in range(2, num_eprice + 5):
    price = 6.3 + random.randint(-5, 10) / 10
    date = "2023-06-17 " + (str(x % 24) if (9 < x % 24) else ("0" + str(x % 24))) + ":" + (str(2 * x % 60) if (9 < 2 * x % 60) else ("0" + str(2 * x % 60))) + ":" + (str(4 * x % 60) if (9 < 4 * x % 60) else ("0" + str(4 * x % 60)))
    file.write(f"insert into eprice(pid,price,date,info) values({x},{round(price, 2)},'{date}','{info}');\n")


uid = 0
pid = 1
flag = True
flag_op = 0
flag_count = 0
change_pid = 1
for x in range(1, num_record):
    if (change_pid < x // (num_record // (num_eprice + 5))):
        while (True):
            t = random.randint(0, 1000) % (num_eprice + 5)
            if (pid <= t):
                pid = t
                change_pid += 1
                break
    if (flag or num_user < flag_count + 1):
        temp = random.randint(0, num_record)
        if (temp < num_record / 1000):
            flag_op = 0
            flag = True
        elif (temp < num_record - 2 * num_record / 1000):
            flag_op = 1
            flag = True
        else:
            flag_op = 2
            flag = False
            flag_count &= 0
            uid &= 0
    flag_count += 1
    op = flag_op
    changes = random.randint(10, 100)
    if (op == 2):
        changes *= 4.3
    s = x // 100
    m = s // 60
    h = m // 60
    date = "2023-06-17 " + (str(h % 24) if (9 < h % 24) else ("0" + str(h % 24))) + ":" + (str(m % 60) if (9 < m % 60) else ("0" + str(m % 60))) + ":" + (str(s % 60) if (9 < s % 60) else ("0" + str(s % 60)))
    pinfo = ("" if (op != 2) else "统一电费录入 ") + info
    if (not flag):
        uid += 1
    else:
        uid = x % num_user if (x % num_user != 0) else 1
    file.write(f"insert into record(rid,op,changes,date,info,pid,uid) values({x},{op},{changes},'{date}','{pinfo}',{pid},{uid});\n")
    if (op == 0):
        file.write(f"update user set money={changes} where uid={uid};\n")
    elif (op == 1):
        file.write(f"update user set money=(select t.money from (select money+{changes} as money from user where uid={uid}) t) where uid={uid};\n")
    else:
        file.write(f"update user set money=(select t.money from (select money-{changes} as money from user where uid={uid}) t) where uid={uid};\n")
file.close()







