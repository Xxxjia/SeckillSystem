<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>秒杀详情页</title>
<%@include file="common/head.jsp"%>
<!-- 静态包含（包含的Jsp会合并作为整个servlet输出）；动态包含会有多个servlet -->
<head>
    <title>秒杀详情页</title>
    <meta name="content-type" content="text/html; charset=UTF-8">
    <%@include file="common/head.jsp"%>
    <%--静态包含，他将会把head.jsp拿过来拼接在一起；动态包含：head.jsp将会作为一个独立的jsp，先把这个jsp独立运行的结果拿过来
    给这个html合并。--%>
</head>
<body>
    <div class="container">
        <div class="panel panel-default text-center">
            <div class="panel-heading">
                <h1>${seckill.name}</h1>
            </div>
            <div class="panel-body">
                <h2 class="text-danger">
                    <%--显示时间--%>
                    <span class="glyphicon glyphicon-time"></span>
                    <%--显示倒计时--%>
                    <span class="glyphicon" id="seckill-box"></span>
                </h2>                          
            </div>
        </div>
    </div>
    <%--登录弹出层，输入电话--%>
    <!-- 完全用前端把用户登录模块做完 -->
    <div id="killPhoneModal" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title text-center">
                        <span class="glyphicon glyphicon-phone"></span>
                    </h3>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-8 col-xs-offset-2">
                            <input type="text" name="killPhone" id="killPhoneKey"
                                   placeholder="填手机号^o^" class="form-control">
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                <!-- 验证信息 -->
                    <span id="killPhoneMessage" class="glyphicon"></span>
                    <button type="button" id="killPhoneBtn" class="btn btn-success">
                        <span class="glyphicon glyphicon-phone"></span>
                        Submit
                    </button>
                </div>
            </div>
        </div>
    </div>
</body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>
<!-- 使用CDN获取公共js http://www.bootcdn。cn -->
<!-- jQuery cookie操作插件 -->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<!-- jQuery cookie倒计时插件 -->
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>

<script src="/seckillFinalDemo/resource/script/seckill.js" type="text/javascript"  charset="UTF-8"></script>

<script type="text/javascript">
    $(function(){
       seckill.detail.init({
           seckillId:${seckill.seckillId},
           startTime:${seckill.startTime.time},//毫秒时间，方便js直接做解析
           endTime:${seckill.endTime.time}
       });
    });

</script>
</html>
