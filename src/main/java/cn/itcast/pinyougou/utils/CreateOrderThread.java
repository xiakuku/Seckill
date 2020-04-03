package cn.itcast.pinyougou.utils;


import cn.itcast.pinyougou.mapper.TbSeckillGoodsMapper;
import cn.itcast.pinyougou.pojo.OrderRecord;
import cn.itcast.pinyougou.pojo.TbSeckillGoods;
import cn.itcast.pinyougou.pojo.TbSeckillOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class CreateOrderThread implements Runnable {

    @Resource
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Resource
    private RedisTemplate redisTemplate;


    @Resource
    private IdWorker  idWorker;


    @Override
    public void run() {

        OrderRecord record = (OrderRecord)redisTemplate.boundListOps(OrderRecord.class.getSimpleName()).rightPop();

        // 已经是 成功要购买的用户
        if(null!=record){
        //1.从redis获取商品
            TbSeckillGoods seckillGoods = (TbSeckillGoods)
                    redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).get(record.getId());
//        //2.判断商品为null或库存<=0，返回商品已售罄
//        if(null == seckillGoods || seckillGoods.getStockCount() <= 0){
//            return new Result(false, "对不起，商品已售罄，请查看其他商品！");
//        }
            //3.创建秒杀订单
            TbSeckillOrder seckillOrder = new TbSeckillOrder();
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setMoney(seckillGoods.getCostPrice());
            seckillOrder.setSeckillId(idWorker.nextId());
            seckillOrder.setSellerId(seckillGoods.getSellerId());
            seckillOrder.setUserId(record.getUserId());
            seckillOrder.setStatus("0"); //未支付
            //4.秒杀订单存入缓存，库存-1
            redisTemplate.boundHashOps(TbSeckillOrder.class.getSimpleName()).put(record.getUserId(), seckillOrder);
            synchronized (CreateOrderThread.class){
                seckillGoods = (TbSeckillGoods)
                        redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).get(record.getId());
                seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
                //5.判断库存是否<=0
                if(seckillGoods.getStockCount() <= 0){
                    //5.1是，更新秒杀商品，保存秒杀订单，删除缓存
                    seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
                    redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).delete(seckillGoods.getId());
                } else {
                    //5.2否，更新秒杀商品缓存
                    redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).put(record.getId(), seckillGoods);
                }
            }

        }


    }
}
