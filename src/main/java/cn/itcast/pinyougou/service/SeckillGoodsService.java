package cn.itcast.pinyougou.service;

import cn.itcast.pinyougou.pojo.Result;
import cn.itcast.pinyougou.pojo.TbSeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    List<TbSeckillGoods> findAll();


    TbSeckillGoods findOne(Long id);


    Result saveOrder(Long id,String userid);
}
