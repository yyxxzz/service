package com.yoho.gateway.utils.constants;

import com.yoho.error.GatewayError;

public class BackPwdPage {

	public static String getBackPwdPage(String path, String code) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("<!DOCTYPE HTML>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n");
		sb.append("<meta property=\"qc:admins\" content=\"1202101235617072516375\" />\n");
		sb.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n");
		sb.append("<meta name=\"author\" content=\"yohobuy.com\"/>\n");
		sb.append("<meta name=\"Copyright\" content=\"yohobuy.com\"/>\n");
		sb.append("<link rel=\"shortcut icon\" href=\"http://www.yohobuy.com/favicon.ico\" type=\"image/x-icon\" />\n");
		sb.append("<script type=\"text/javascript\">var _oztime = (new Date()).getTime();</script>\n");
		sb.append(" <title></title><link href=\"http://static.yohobuy.com/css/v3/common.css?v=20151026\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >\n");
		sb.append("<link href=\"http://static.yohobuy.com/css/style.css?v=20121106\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >\n");
		sb.append("<link href=\"http://static.yohobuy.com/css/v3/head-fixed-new.css?2015103017\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >\n");
		sb.append("<link href=\"http://static.yohobuy.com/css/yohofamily/reset-pwd.css?v=20150828\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" >\n");
		sb.append("<link href=\"http://static.yohobuy.com/css/formValidator.css?v=20121102\" media=\"screen\" rel=\"stylesheet\" type=\"text/css\" ><script type=\"text/javascript\" src=\"http://static.yohobuy.com/static/jquery/jquery.min.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"http://static.yohobuy.com/static/jquery/jquery.plugins.js?v=20150421\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"http://static.yohobuy.com/js/v4/Qin.js?v=20151106\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"http://static.yohobuy.com/js/shopping/newcart.js?v=201512031844\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"http://static.yohobuy.com/js/jq_plugins/formValidator/formValidator.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"http://static.yohobuy.com/js/jq_plugins/formValidator/formValidatorRegex.js\"></script>\n");
		sb.append("<script type=\"text/javascript\" src=\"http://static.yohobuy.com/js/passport/passport_back.js\"></script><script type=\"text/javascript\">var _ozprm;</script><script type=\"text/javascript\">\n");
		sb.append("var apiDomain = 'http://api.open.yohobuy.com';\n");
		sb.append("var __custom = {\"online\":\"off\"};\n");
		sb.append("</script>\n");
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("<!--\n");
		sb.append("var _ozuid = '';\n");
		sb.append("var _profile = 'username=';\n");
		sb.append("//-->\n");
		sb.append("</script>\n");
		sb.append("<!--[if lt IE 7]>\n");
		sb.append("    <script defer type=\"text/javascript\" src=\"http://static.yohobuy.com/js/v3/pngfix.js\"></script>\n");
		sb.append("<![endif]-->\n");
		sb.append("\n");
		sb.append("<style type=\"text/css\">\n");
		sb.append("@font-face {\n");
		sb.append("	font-family: 'icomoon';\n");
		sb.append("	src:url('/fonts/icomoon.eot');\n");
		sb.append("	src:url('/fonts/icomoon.eot?#iefix') format('embedded-opentype'),\n");
		sb.append("		url('/fonts/icomoon.ttf') format('truetype'),\n");
		sb.append("		url('/fonts/icomoon.woff') format('woff'),\n");
		sb.append("		url('/fonts/icomoon.svg#icomoon') format('svg');\n");
		sb.append("	font-weight: normal;\n");
		sb.append("	font-style: normal;\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("[class*=\"icon-\"] {\n");
		sb.append("	font-family: 'icomoon';\n");
		sb.append("	speak: none;\n");
		sb.append("	font-style: normal;\n");
		sb.append("	font-weight: normal;\n");
		sb.append("	font-variant: normal;\n");
		sb.append("	text-transform: none;\n");
		sb.append("	line-height: 1;\n");
		sb.append("\n");
		sb.append("	/* Better Font Rendering =========== */\n");
		sb.append("	-webkit-font-smoothing: antialiased;\n");
		sb.append("	-moz-osx-font-smoothing: grayscale;\n");
		sb.append("}\n");
		sb.append(".ifont{\n");
		sb.append("	font-family: \"icomoon\" ;\n");
		sb.append("	display:inline-block;\n");
		sb.append("}\n");
		sb.append(".ifont10{\n");
		sb.append("	font-family: \"icomoon\" ;\n");
		sb.append("	display:inline-block;\n");
		sb.append("	font-size: 10px;\n");
		sb.append("	line-height: 10px;\n");
		sb.append("	-webkit-transform:scale(0.8);\n");
		sb.append("}\n");
		sb.append("\n");
		sb.append("</style>\n");
		sb.append("\n");
		sb.append("</head>\n");
		sb.append("<body class=\"body990\">\n");
		sb.append("<!-- Google Tag Manager -->\n");
		sb.append("<noscript><iframe src=\"//www.googletagmanager.com/ns.html?id=GTM-W958MG\"\n");
		sb.append("height=\"0\" width=\"0\" style=\"display:none;visibility:hidden\"></iframe></noscript>\n");
		sb.append("<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':\n");
		sb.append("new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],\n");
		sb.append("j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=\n");
		sb.append("'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);\n");
		sb.append("})(window,document,'script','dataLayer','GTM-W958MG');\n");
		sb.append("function getSource(column,postition,event){\n");
		sb.append("	try {\n");
		sb.append("			dataLayer.push({\n");
		sb.append("			     'louceng': column,\n");
		sb.append("			     'weizhi': postition,\n");
		sb.append("			     'event': event\n");
		sb.append("			});\n");
		sb.append("		} catch (e) {}\n");
		sb.append("}</script>\n");
		sb.append("<!-- End Google Tag Manager -->\n");
		sb.append("<!--页头开始-->\n");
		sb.append("<div class=\"header-simple\">\n");
		sb.append("	  <div class=\"screen990\"><!--screen990 screen1240-->\n");
		sb.append("		<div class=\"clear\">\n");
		sb.append("			<div class=\"left logo\">\n");
		sb.append("				<a href=\"http://www.yohobuy.com\"><img alt=\"YOHO!有货\" src=\"http://static.yohobuy.com/newheader/img/logo_e.png\"/></a>\n");
		sb.append("			</div>\n");
		sb.append("			<div class=\"right\">\n");
		sb.append("				<ul class=\"simple-items clear\">\n");
		sb.append("					<li class=\"left\" id=\"loginBox\"></li>\n");
		sb.append("					<li class=\"left relative my-yoho\" id=\"homePage\"><span class=\"item\"><a href=\"http://www.yohobuy.com/home?t=1452043810.3181\" class=\"rgb2\">MY有货<span class=\"ifont10 arrow-down\" id=\"homeNavArrow\">]</span></a></span><p class=\"absolute\" style=\"display:none;\" id=\"homeNav\"><a href=\"http://www.yohobuy.com/home/favorite?t=1452043810.3181\" class=\"rgb2\">我的收藏</a><a href=\"http://www.yohobuy.com/home/coupons?t=1452043810.3181\" class=\"rgb2\">优惠券</a></p></li>\n");
		sb.append("					<li class=\"left\"><span class=\"item\"><a href=\"http://www.yohobuy.com/home/orders?t=1452043810.3181\" class=\"rgb2\">订单中心</a></span></li>\n");
		sb.append("					<li class=\"left\"><span class=\"item\"><a href=\"http://www.yohobuy.com/help\" class=\"rgb2\">帮助中心</a></span></li>\n");
		sb.append("					<li class=\"left\"><span class=\"rgb2 item\"><span class=\"ifont\">p</span>&nbsp;<strong>400-889-9646</strong></span></li>\n");
		sb.append("				</ul>\n");
		sb.append("			</div>\n");
		sb.append("		</div>\n");
		sb.append("	  </div>\n");
		sb.append("	</div>\n");
		sb.append("<!--页头结束-->\n");
		sb.append("\n");
		sb.append("        <div id=\"content-container\" class=\"content-container\">\n");
		sb.append("            <div class=\"logo-container\">\n");
		sb.append("                <div class=\"sub-container\">\n");
		sb.append("                    <div class=\"logo\"></div>\n");
		sb.append("                </div>\n");
		sb.append("            </div>\n");
		sb.append("            <div id=\"page-content\" class=\"page-content\">\n");
		sb.append("                <div class=\"page reset-pwd-page\">\n");
		sb.append("				    <h2 class=\"title2\">重置密码</h2>\n");
		sb.append("				    <form id=\"reset-pwd-form\" class=\"reset-pwd-form\" method=\"POST\">\n");
		sb.append("				        <ul>\n");
		sb.append("				            <li class=\"input-container-li po-re\">\n");
		sb.append("                                <input id=\"pwd\" class=\"input va pwd\" type=\"password\" name=\"pwd\" placeholder=\"新密码\" maxlength=\"20\">\n");
		sb.append("                                <div class=\"pwd-intensity-container\">\n");
		sb.append("                                    <span class=\"pwd-intensity low\">低</span>\n");
		sb.append("                                    <span class=\"pwd-intensity mid\">中</span>\n");
		sb.append("                                    <span class=\"pwd-intensity high\">高</span>\n");
		sb.append("                                </div>\n");
		sb.append("                                <div id=\"pwd-tips\" class=\"pwd-tips hide\" >\n");
		sb.append("                                    <div class=\"default\" id=\"pwd-tip1\">密码只支持6-20位字符</div>\n");
		sb.append("                                    <div class=\"default\" id=\"pwd-tip2\">只能由字母、 数字组合</div>\n");
		sb.append("                                </div>\n");
		sb.append("                                <span id=\"pwd-err\" class=\"hide err-tip\"></span>\n");
		sb.append("                            </li>\n");
		sb.append("				            <li class=\"input-container-li clearfix po-re\">\n");
		sb.append("				                <input id=\"re-input\" class=\"input va re-input repwd\" type=\"password\" name=\"re-input\" placeholder=\"再次输入\" maxlength=\"20\">\n");
		sb.append("                                <span id=\"repwd-err\" class=\"hide err-tip\"></span>\n");
		sb.append("				            </li>\n");
		sb.append("				            <li class=\"input-container-li clearfix\">\n");
		sb.append("				            <input type=\"hidden\" name=\"code\" value=\"\">\n");
		sb.append("				                <input id=\"reset-pwd-btn\" class=\"btn reset-pwd-btn\" type=\"button\" value=\"提交\" disabled>\n");
		sb.append("				            </li>\n");
		sb.append("				        </ul>\n");
		sb.append("				    </form>\n");
		sb.append("				</div>\n");
		sb.append("            </div>\n");
		sb.append("        </div>\n");
		sb.append("    <script src=\"http://cdn.bootcss.com/jquery/1.9.0/jquery.min.js\"></script>\n");
		sb.append("    <script type=\"text/javascript\" src=\"http://static.yohobuy.com/js/v3/Qin.js?v=20150525\"></script>\n");
		sb.append("    <script src=\"http://static.yohobuy.com/js/passport/pwd-strength.js\"></script>\n");
		sb.append("    <script src=\"http://static.yohobuy.com/js/passport/jquery.placeholder.js\"></script>\n");
		sb.append("    <script>\n");
		sb.append("    	$(function() {\n");
		sb.append("			$(\"#reset-pwd-form #reset-pwd-btn\").click(function() {\n");
		sb.append("				$.post(\"").append("").append("/jsp/updatePwd\", {\n");
		sb.append("					\"pwd\" : $(\"#reset-pwd-form #pwd\").val(),\n");
		sb.append("					\"re-input\" : $(\"#reset-pwd-form #re-input\").val(),\n");
		sb.append("					\"code\" : '").append(code).append("'\n");
		sb.append("				}, function(data) {\n");
		sb.append("					if (data.code == ").append(GatewayError.CODE_SUCCESS.getCode()).append(") {\n");
		sb.append("						alert(\"密码修改成功！\");\n");
		sb.append("						window.location = \"").append(Constants.YOHO_HOME_URL).append("\";\n");
		sb.append("					} else {\n");
		sb.append("						alert(data.message);\n");
		sb.append("					}\n");
		sb.append("				}, \"json\");\n");
		sb.append("			});\n");
		sb.append("    		var $pwd = $('#pwd'),\n");
		sb.append("    			$repwd = $('#re-input'),\n");
		sb.append("    			$next = $('#reset-pwd-btn'),\n");
		sb.append("    			$pwdErr = $('#pwd-err'),\n");
		sb.append("    			$repwdErr = $('#repwd-err'),\n");
		sb.append("    			$pwdTips = $('#pwd-tips');\n");
		sb.append("                \n");
		sb.append("    		var hasNoErrPw = false;\n");
		sb.append("            \n");
		sb.append("    		var $pwdIntensity = $('.pwd-intensity'),\n");
		sb.append("    			$pwdParent = $pwdIntensity.closest('.pwd-intensity-container'),\n");
		sb.append("    			$pwdTip1 = $('#pwd-tip1');\n");
		sb.append("                \n");
		sb.append("            //IE8 placeholder\n");
		sb.append("			$('[placeholder]').placeholder();\n");
		sb.append("            \n");
		sb.append("    		function pwdKeyupEvt() {\n");
		sb.append("    			var pwd = $pwd.val(),\n");
		sb.append("    				pwdStrength = computeComplex(pwd),\n");
		sb.append("    				level = 0;\n");
		sb.append("    			//TODO:自定义密码强度规则,需要修正\n");
		sb.append("    			if (pwdStrength === 0) {\n");
		sb.append("    				level = 0;\n");
		sb.append("    			} else if (pwdStrength <= 10) {\n");
		sb.append("    				level = 1;\n");
		sb.append("    			} else if (pwdStrength <= 20) {\n");
		sb.append("    				level = 2;\n");
		sb.append("    			} else {\n");
		sb.append("    				level = 3;\n");
		sb.append("    			}\n");
		sb.append("    			switch (level) {\n");
		sb.append("    				case 0:\n");
		sb.append("    					$pwdParent.removeClass('red yellow green');\n");
		sb.append("    					$pwdIntensity.removeClass('color');\n");
		sb.append("    					break;\n");
		sb.append("    				case 1:\n");
		sb.append("    					$pwdParent.addClass('red').removeClass('yellow green');\n");
		sb.append("    					$pwdIntensity.filter('.low').addClass('color');\n");
		sb.append("    					$pwdIntensity.filter('.mid,.high').removeClass('color');\n");
		sb.append("    					break;\n");
		sb.append("    				case 2:\n");
		sb.append("    					$pwdParent.addClass('yellow').removeClass('red green');\n");
		sb.append("    					$pwdIntensity.filter('.low,.mid').addClass('color');\n");
		sb.append("    					$pwdIntensity.filter('.high').removeClass('color');\n");
		sb.append("    					break;\n");
		sb.append("    				case 3:\n");
		sb.append("    					$pwdParent.addClass('green').removeClass('yellow red');\n");
		sb.append("    					$pwdIntensity.addClass('color');\n");
		sb.append("    					break;\n");
		sb.append("    			}\n");
		sb.append("    			//\n");
		sb.append("    			if (pwd === '') {\n");
		sb.append("    				hasNoErrPw = false;\n");
		sb.append("    				$pwdTip1.removeClass('red yes no').addClass('default');\n");
		sb.append("    			} else {\n");
		sb.append("    				if (pwd.length < 6 || pwd.length > 20) {\n");
		sb.append("    					hasNoErrPw = false;\n");
		sb.append("    					$pwdTip1.removeClass('default yes').addClass('no red');\n");
		sb.append("    				} else {\n");
		sb.append("    					hasNoErrPw = true;\n");
		sb.append("    					$pwdTip1.removeClass('default no red').addClass('yes');\n");
		sb.append("    				}\n");
		sb.append("    				//提示2不做验证\n");
		sb.append("    			}\n");
		sb.append("    		}\n");
		sb.append("    			\n");
		sb.append("    		$('.va').keyup(function() {\n");
		sb.append("    			var pass = true;\n");
		sb.append("                if ($(this).hasClass('pwd')) {\n");
		sb.append("                    pwdKeyupEvt();\n");
		sb.append("                } else {\n");
		sb.append("                    if ($(this).val() === '') {\n");
		sb.append("        				pass = false;\n");
		sb.append("        			}\n");
		sb.append("                }\n");
		sb.append("                if (pass && hasNoErrPw && $pwd.val() === $repwd.val()) {\n");
		sb.append("					pass = true;\n");
		sb.append("				} else {\n");
		sb.append("                    pass = false;\n");
		sb.append("                }\n");
		sb.append("                if (pass) {\n");
		sb.append("                    $next.removeClass('disable').prop('disabled', false);\n");
		sb.append("                } else {\n");
		sb.append("                    $next.addClass('disable').prop('disabled', true);\n");
		sb.append("                }\n");
		sb.append("    		}).blur(function() {\n");
		sb.append("    			var v = $(this).val();\n");
		sb.append("    			if ($(this).hasClass('pwd')) {\n");
		sb.append("    				if (v === '') {\n");
		sb.append("    					$(this).addClass('error');\n");
		sb.append("    					$pwdErr.removeClass('hide').text('请输入密码');\n");
		sb.append("    				} else if (v.length < 6 || v.length > 20) {\n");
		sb.append("    					$(this).addClass('error');\n");
		sb.append("    					$pwdErr.removeClass('hide').text('密码只支持6-20位');\n");
		sb.append("    				} else {\n");
		sb.append("    					$pwdErr.addClass('hide');\n");
		sb.append("    					if ($repwd.val() !== '') {\n");
		sb.append("    						if (v !== $repwd.val()) {\n");
		sb.append("    							$repwd.addClass('error');\n");
		sb.append("    							$repwdErr.removeClass('hide').text('两次密码输入不一致，请重新输入');\n");
		sb.append("    						} else {\n");
		sb.append("    							$repwd.removeClass('error');\n");
		sb.append("    							$repwdErr.addClass('hide');\n");
		sb.append("    						}\n");
		sb.append("    					}\n");
		sb.append("    				}\n");
		sb.append("    			} else {\n");
		sb.append("    				if (v === '') {\n");
		sb.append("    					$(this).addClass('error');\n");
		sb.append("    					$repwdErr.removeClass('hide').text('请输入密码确认');\n");
		sb.append("    				} else {\n");
		sb.append("    					if ($pwd.val() !== '' && v !== $pwd.val()) {\n");
		sb.append("    						$(this).addClass('error');\n");
		sb.append("    						$repwdErr.removeClass('hide').text('两次密码输入不一致，请重新输入');\n");
		sb.append("    					} else {\n");
		sb.append("    						$(this).removeClass('error');\n");
		sb.append("    						$repwdErr.addClass('hide')\n");
		sb.append("    					}\n");
		sb.append("    				}\n");
		sb.append("    			}\n");
		sb.append("    		}).focus(function() {\n");
		sb.append("    			$(this).removeClass('error');\n");
		sb.append("                //focus后错误提示隐藏\n");
		sb.append("				if ($(this).hasClass('pwd')) {\n");
		sb.append("					$pwdErr.addClass('hide');\n");
		sb.append("				} else {\n");
		sb.append("					$repwdErr.addClass('hide');\n");
		sb.append("				}\n");
		sb.append("    		});\n");
		sb.append("    		\n");
		sb.append("    		$pwd.focus(function() {\n");
		sb.append("    			$pwdErr.addClass('hide');\n");
		sb.append("    			$pwdTips.removeClass('hide');\n");
		sb.append("    		}).blur(function() {\n");
		sb.append("    			$pwdTips.addClass('hide');\n");
		sb.append("    		});\n");
		sb.append("            \n");
		sb.append("            $('#pwd, #repwd').keydown(function(e) {\n");
		sb.append("                var code = e.keyCode || e.which;\n");
		sb.append("                //空格输入过滤\n");
		sb.append("				if (code === 32) {\n");
		sb.append("					e.preventDefault();\n");
		sb.append("					return;\n");
		sb.append("				}\n");
		sb.append("            });\n");
		sb.append("    	});\n");
		sb.append("    </script><div class=\"footer2013\">\n");
		sb.append("		<div class=\"promise\">\n");
		sb.append("		  <div class=\"screen clear\">\n");
		sb.append("			<div class=\"left\"><span class=\"ifont rgbf\">g</span><span class=\"red\">100%</span><span class=\"rgbf\">品牌授权正品</span></div>\n");
		sb.append("			<div class=\"left\"><span class=\"ifont rgbf\">L</span><span class=\"red\">7天</span><span class=\"rgbf\">无理由退换货</span></div>\n");
		sb.append("			<div class=\"left\"><span class=\"ifont rgbf\">p</span><span class=\"rgbf\">客服电话：</span><span class=\"red\">400-889-9646</span>&nbsp;&nbsp;<span class=\"rgb9\">08:00-22:30(周一至周日)</span><span><a href=\"http://chat80.live800.com/live800/chatClient/chatbox.jsp?companyID=493979&configID=123576&jid=9277320930\" target=\"_blank\" style=\"color:red\">&nbsp;&nbsp;在线客服</a></span></div>\n");
		sb.append("			<div class=\"right subscribe footer-right\"><input class=\"rgb6 top\" name=\"subscriberBox\" id=\"subscriberBox\" value=\"订阅我们的邮件\" /><a href=\"javascript:void(0);\" id=\"subscriberBtn\" class=\"ifont rgbf\">m</a><!--<input class=\"rgb6 top wrong\" value=\"订阅我们的邮件\" /><a href=\"\" class=\"ifont rgbf done\">=</a>--></div>\n");
		sb.append("		  </div>\n");
		sb.append("		</div>	\n");
		sb.append("		<div class=\"footer-help\">\n");
		sb.append("		  <div class=\"screen clear\">\n");
		sb.append("			<div class=\"left\">\n");
		sb.append("				<ul class=\"clear\">\n");
		sb.append("					<li class=\"left\">\n");
		sb.append("						<p><span>新手指南</span></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=81#help_b00reg\" target=\"_blank\">注册登录</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=83\" target=\"_blank\">选购商品</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=103\" target=\"_blank\">订单支付</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=85\" target=\"_blank\">收货退款</a></p>\n");
		sb.append("					</li>\n");
		sb.append("					<li class=\"left\">\n");
		sb.append("						<p><span>会员中心</span></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=87\" target=\"_blank\">YOHO币</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=91\" target=\"_blank\">会员制度</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=89\" target=\"_blank\">账户管理</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=93\" target=\"_blank\">密码管理</a></p>\n");
		sb.append("					</li>\n");
		sb.append("					<li class=\"left\">\n");
		sb.append("						<p><span>购物指南</span></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=95\" target=\"_blank\">全球购专区</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=97\" target=\"_blank\">尺码选择</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=99\" target=\"_blank\">发票</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=101\" target=\"_blank\">商品咨询</a></p>\n");
		sb.append("					</li>\n");
		sb.append("					<li class=\"left\">\n");
		sb.append("						<p><span>支付方式</span></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=105\" target=\"_blank\">在线支付</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=107\" target=\"_blank\">货到付款</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=109\" target=\"_blank\">优惠券</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=111\" target=\"_blank\">YOHO币支付</a></p>\n");
		sb.append("					</li>\n");
		sb.append("					<li class=\"left\">\n");
		sb.append("						<p><span>配送方式</span></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=113\" target=\"_blank\">配送时间</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=115\" target=\"_blank\">配送范围</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=119\" target=\"_blank\">顺丰速运</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=117\" target=\"_blank\">商品验收与签收</a></p>\n");
		sb.append("					</li>\n");
		sb.append("					<li class=\"left\">\n");
		sb.append("						<p><span>售后服务</span></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=121\" target=\"_blank\">退换货政策</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=123\" target=\"_blank\">退换货流程</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=125\" target=\"_blank\">退款方式与时效</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=127\" target=\"_blank\">投诉与建议</a></p>\n");
		sb.append("					</li>\n");
		sb.append("					<li class=\"left\">\n");
		sb.append("						<p><span>APP常见问题</span></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=133\" target=\"_blank\">IPhone版</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=135\" target=\"_blank\">Android版</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=137\" target=\"_blank\">wap版</a></p>\n");
		sb.append("						<p><a href=\"http://www.yohobuy.com/help/?category_id=139\" target=\"_blank\">IPAD版</a></p>\n");
		sb.append("					</li>\n");
		sb.append("				</ul>\n");
		sb.append("			</div>\n");
		sb.append("		  </div>\n");
		sb.append("		</div>\n");
		sb.append("		<div class=\"footer-link\">\n");
		sb.append("			<div class=\"screen clear\">\n");
		sb.append("				<div class=\"left right-flag\">\n");
		sb.append("					<a href=\"https://ss.knet.cn/verifyseal.dll?sn=e14021832010046477dka7000000&ct=df&a=1&pa=0.5902942178957805\" target=\"_blank\" rel=\"nofollow\"><img src=\"http://static.yohobuy.com/images/v3/icon/credit-flag3.png\" /></a>\n");
		sb.append("					<a href=\"http://www.isc.org.cn/\" target=\"_blank\" rel=\"nofollow\"><img src=\"http://static.yohobuy.com/images/v3/icon/isc2.png\" /></a>\n");
		sb.append("				</div>\n");
		sb.append("				<div class=\"left about-us\">\n");
		sb.append("					<p><a href=\"http://www.yohobuy.com\">返回首页</a><span>|</span><a href=\"http://www.yohobuy.com\">YOHO!有货</a><span>|</span><a href=\"http://www.yohobuy.com/newpower.html\">新力传媒</a><span>|</span><a href=\"http://www.yohobuy.com/contact.html\">联系我们</a><span>|</span><a href=\"http://shop.yohobuy.com/settled\">商家入驻</a><span>|</span><a href=\"http://www.yohobuy.com/privacy.html\">隐私条款</a><span>|</span><a href=\"http://www.yohobuy.com/link.html\">友情链接</a></p>\n");
		sb.append("					<p>CopyRight © 2007-2016 南京新与力文化传播有限公司 <a href=\"http://www.miibeian.gov.cn/\" target=\"_blank\" style=\"color:#666\">苏ICP备09011225号</a> NewPower Co. 版权所有 经营许可证编号：苏B2-20120395</p>\n");
		sb.append("				</div>\n");
		sb.append("				\n");
		sb.append("			</div>\n");
		sb.append("		</div>	\n");
		sb.append("	</div><script type=\"text/html\" id=\"tmpl-nologin\">\n");
		sb.append("<span>Hi~</span>[<a href=\"http://www.yohobuy.com/signin.html\" class=\"list-a login-out\">请登录</a>]&nbsp;[<a href=\"http://www.yohobuy.com/reg.html\" class=\"list-a login-out\">免费注册</a>]\n");
		sb.append("</script>\n");
		sb.append("<script type=\"text/html\" id=\"tmpl-login\">\n");
		sb.append("<span>Hi~<a href=\"http://www.yohobuy.com/home?t=1452043810.3187\">{{=it.user_name}}</a></span>&nbsp;{{? it.is_login == 1}}[<a href=\"{{=it.logout}}\" class=\"list-a login-out\">退出</a>]{{??}}[<a href=\"http://www.yohobuy.com/signin.html\" class=\"list-a login-out\">请登录</a>]&nbsp;[<a href=\"http://www.yohobuy.com/reg.html\" class=\"list-a login-out\">免费注册</a>]{{?}}\n");
		sb.append("</script>\n");
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("//根据浏览器宽度选择网页尺寸 1240 | 990\n");
		sb.append("$(document).ready(function(){\n");
		sb.append("	$('#homePage').showLayout({showLayoutId : '#homeNav',eventType : 'mouse',onEvent:function(isHidden){if(isHidden == true){$('#homeNavArrow').html(']');$('#homePage').removeClass('cur');}else{$('#homeNavArrow').html('[');$('#homePage').addClass('cur');}}});\n");
		sb.append("	$.userInfo('loginBox', '_UID', 'tmpl-login', 'tmpl-nologin');\n");
		sb.append("	$('#subscriberBtn').click(function(){\n");
		sb.append("		var email = $('#subscriberBox').val();\n");
		sb.append("		if(!/^[.\\-_a-zA-Z0-9]+@[\\-_a-zA-Z0-9]+\\.[a-zA-Z0-9]/.test(email)){\n");
		sb.append("			$('#subscriberBox').animate({'backgroundColor':'#ffa7a7'}, 500).animate({'backgroundColor':'#ffffff'}, 500).animate({'backgroundColor':'#ffa7a7'}, 500).animate({'backgroundColor':'#ffffff'}, 500);\n");
		sb.append("			$('#subscriberBox').val('').focus();\n");
		sb.append("			return false;\n");
		sb.append("		}\n");
		sb.append("		var uid = $.uid('_UID');\n");
		sb.append("		$.getData(apiDomain,{'method':'open.subscriber.subscriber','email':email,'uid':uid},function(data){\n");
		sb.append("			if(data.result == 1){\n");
		sb.append("				$('#subscriberBox').addClass('done').val('已订阅到:'+email);\n");
		sb.append("				$('#subscriberBtn').text('=').addClass('done').click(function(){\n");
		sb.append("					return false;\n");
		sb.append("				});\n");
		sb.append("				return false;\n");
		sb.append("			}$('#subscriberBox').animate({'backgroundColor':'#ffa7a7'}, 500).animate({'backgroundColor':'#ffffff'}, 500).animate({'backgroundColor':'#ffa7a7'}, 500).animate({'backgroundColor':'#ffffff'}, 500);$('#subscriberBox').val('').focus();return false;\n");
		sb.append("		});\n");
		sb.append("	});\n");
		sb.append("	$('#subscriberBox').focus(function(){$(this).val('');$('#subscriberBtn').text('m').removeClass('done');});\n");
		sb.append("});\n");
		sb.append("</script>\n");
		sb.append("<!--页尾结束-->\n");
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("    var _hmt = _hmt || [];\n");
		sb.append("    (function() {\n");
		sb.append("        var hm = document.createElement(\"script\");\n");
		sb.append("        hm.src = \"//hm.baidu.com/hm.js?c6ee7218b8321cb65fb2e98f284d8311\";\n");
		sb.append("        var s = document.getElementsByTagName(\"script\")[0];\n");
		sb.append("        s.parentNode.insertBefore(hm, s);\n");
		sb.append("    })();\n");
		sb.append("</script>\n");
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("var _hmt = _hmt || [];\n");
		sb.append("(function() {\n");
		sb.append("  var hm = document.createElement(\"script\");\n");
		sb.append("  hm.src = \"//hm.baidu.com/hm.js?65dd99e0435a55177ffda862198ce841\";\n");
		sb.append("  var s = document.getElementsByTagName(\"script\")[0];\n");
		sb.append("  s.parentNode.insertBefore(hm, s);\n");
		sb.append("})();\n");
		sb.append("var  _ozuid = $.uid('_UID') || \"\";\n");
		sb.append("</script>\n");
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("(\n");
		sb.append("  function(w,d,s,j,f)\n");
		sb.append("  {\n");
		sb.append("     w['YohoAcquisitionObject'] = f;\n");
		sb.append("     w[f] = function()\n");
		sb.append("     {\n");
		sb.append("       w[f].p = arguments;\n");
		sb.append("     };\n");
		sb.append("     var a=d.createElement(s);\n");
		sb.append("     var m=d.getElementsByTagName(s)[0];\n");
		sb.append("     a.async=1;\n");
		sb.append("     a.src=j;\n");
		sb.append("     m.parentNode.insertBefore(a,m);\n");
		sb.append("  }\n");
		sb.append(" )(window,document,'script','http://cdn.yoho.cn/yas-jssdk/1.0.9/yas.js','_yas');\n");
		sb.append("_yas(1*new Date(),'1.0.9','yohobuy_web',_ozuid,'');\n");
		sb.append("</script>\n");
		sb.append("<script src=\"http://static.yohobuy.com/js/analytics/analysis.js?v=20140527\"></script>\n");
		sb.append("<script src=\"http://static.yohobuy.com/js/v3/o_code.js?v=20150420\"></script>\n");
		sb.append("<script type=\"text/javascript\">\n");
		sb.append("var _gaq = _gaq || [];\n");
		sb.append("_gaq.push(['_setAccount', 'UA-46357914-14']);\n");
		sb.append(" _gaq.push(['_trackPageview']);\n");
		sb.append("(function() {\n");
		sb.append("var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n");
		sb.append("ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n");
		sb.append("var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n");
		sb.append(" })();\n");
		sb.append("</script>\n");
		sb.append("<script>\n");
		sb.append("         window._py = window._py||[];\n");
		sb.append("         window._py.push(['a', 'MC..o8vMMWxEXDCiqYckD81lUX']);\n");
		sb.append("         window._py.push(['domain','stats.ipinyou.com']);\n");
		sb.append("         if(typeof _goodsData!='undefined')\n");
		sb.append("         window._py.push(['pi',_goodsData]);\n");
		sb.append("         window._py.push(['e','']);\n");
		sb.append("         -function(d){\n");
		sb.append("         var f = 'https:' == d.location.protocol;var c = d.createElement('script');c.type='text/javascript';c.async=1;\n");
		sb.append("         c.src=(f ? 'https' : 'http') + '://'+(f?'fm.ipinyou.com':'fm.p0y.cn')+'/j/t/adv.js';\n");
		sb.append("         var h = d.getElementsByTagName(\"script\")[0];h.parentNode.insertBefore(c, h);\n");
		sb.append("         }(document);\n");
		sb.append("</script>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");
		return sb.toString();
	}
}