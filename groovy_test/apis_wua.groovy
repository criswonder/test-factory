String str="mtop.taobao.idle.topic.publish_1.0^mtop.taobao.idle.item.publish.picurl_1.0^mtop.taobao.idle.welfare.topic.publish_1.0^mtop.taobao.idle.content.question.publish_1.0^mtop.taobao.idle.book.action_1.0^mtop.taobao.idle.localtrade.applymeet_1.0^mtop.taobao.idle.comment.publish_1.0^mtop.taobao.idle.comment.publish_2.0^mtop.taobao.idle.comment.reply_1.0^mtop.taobao.idle.comment.reply_2.0^mtop.taobao.idle.fqa.comment.publish_1.0^mtop.idle.x.message.message.send_1.0^mtop.taobao.idle.message.chat.send_1.0^com.taobao.idle.message.chat.send_2.0^com.taobao.idle.message.chat.send_3.0^mtop.taobao.idle.faq.comment.like_1.0^com.taobao.idle.unfavor.item_1.0^com.taobao.idle.favor.item_1.0^mtop.taobao.idle.impress.create_1.0^mtop.taobao.idle.attention.relation_1.0^com.taobao.idle.trade.createOrder_1.0"

String[] apis = str.split("\\^")
println apis.length
apis.each{it ->
    println it
}
for(int i=0;i<apis.length;i++){
    println apis[i]
}
//apis.each->println it