package cn.itcast.pinyougou.quartz.task;

import cn.itcast.pinyougou.mapper.TbSeckillGoodsMapper;
import cn.itcast.pinyougou.pojo.TbSeckillGoods;
import cn.itcast.pinyougou.pojo.TbSeckillGoodsExample;
import cn.itcast.pinyougou.utils.SystemConst;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Component
public class MyTask {



    @Resource
    private TbSeckillGoodsMapper seckillGoodsMapper;


    @Resource
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/5 * * * * ?")
    public void excTask(){
        System.out.println("MyTask.excTask");

        TbSeckillGoodsExample seckillGoodsExample =new TbSeckillGoodsExample();


        seckillGoodsExample=new TbSeckillGoodsExample();

        Date date =new Date();

        TbSeckillGoodsExample.Criteria criteria = seckillGoodsExample.createCriteria();


        criteria.andStatusEqualTo("1")
                .andStockCountGreaterThan(0)
                .andStartTimeLessThanOrEqualTo(date)
                .andEndTimeGreaterThan(date);

        List<TbSeckillGoods> tbSeckillGoods = seckillGoodsMapper.selectByExample(seckillGoodsExample);

        for(TbSeckillGoods goods:tbSeckillGoods){
            redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).put(goods.getId(),goods);

            createQueue(goods.getId(),goods.getStockCount());
        }

    }

    private void createQueue(Long id, Integer stockCount) {
        if(stockCount>0){
            for(int i=0;i<stockCount;i++){
                redisTemplate.boundListOps(SystemConst.CONST_SBCKGOODS_ID_PREFIX+id).leftPush(id);
            }
        }
    }
}
