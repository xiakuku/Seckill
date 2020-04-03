package cn.itcast.pinyougou.service.impl;

import cn.itcast.pinyougou.mapper.TbSeckillGoodsMapper;
import cn.itcast.pinyougou.pojo.OrderRecord;
import cn.itcast.pinyougou.pojo.Result;
import cn.itcast.pinyougou.pojo.TbSeckillGoods;
import cn.itcast.pinyougou.service.SeckillGoodsService;
import cn.itcast.pinyougou.utils.CreateOrderThread;
import cn.itcast.pinyougou.utils.IdWorker;
import cn.itcast.pinyougou.utils.SystemConst;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executor;


@Service
@Transactional
public class SeckillGoodsServiceImpl implements SeckillGoodsService {



    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public List<TbSeckillGoods> findAll() {
        return redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).values();
    }

    @Override
    public TbSeckillGoods findOne(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).get(id);
    }


    @Resource
    private IdWorker  idWorker;

    @Resource
    private TbSeckillGoodsMapper seckillGoodsMapper;


    @Resource
    private Executor executor;


    @Resource
    private CreateOrderThread createOrderThread;

    @Override
    public Result saveOrder(Long id,String userId) {

        // 查看用户已经下单，使用Redis 的set集合来实现

        Boolean member = redisTemplate.boundSetOps(SystemConst.CONST_USER_ID_PREFIX + id).isMember(userId);

        if(member){
            // 正在排队，或者未支付，提示用户正在排队，或者排队
            return new Result(false,"对不起，你正在排队，等待支付，请尽快支付");
        }

        // n个商品，取出其中一个
        Object goods = redisTemplate.boundListOps(SystemConst.CONST_SBCKGOODS_ID_PREFIX + id).rightPop();

        if(goods==null){
            return new Result(false, "对不起，商品已售罄，请查看其他商品！");
        }

        // 将用户入用户集合
        redisTemplate.boundSetOps(SystemConst.CONST_USER_ID_PREFIX+id).add(userId);
        // 创建订单信息， 用户id 和商品id 放到OrderRecord 队列集合中
        OrderRecord orderRecord =new OrderRecord(userId, id);
        redisTemplate.boundListOps(OrderRecord.class.getSimpleName()).leftPush(orderRecord);
        executor.execute(createOrderThread);
        return new Result(true, "秒杀成功，请您尽快支付！");
    }
}
