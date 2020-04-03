package cn.itcast.pinyougou.controller;


import cn.itcast.pinyougou.pojo.Result;
import cn.itcast.pinyougou.pojo.TbSeckillGoods;
import cn.itcast.pinyougou.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Resource
    private SeckillGoodsService seckillGoodsService;


    @RequestMapping("/findAll")
    public List<TbSeckillGoods> findAll(){

        List<TbSeckillGoods> all = seckillGoodsService.findAll();
        return all;
    }

    @RequestMapping("/findOne/{id}")
    public TbSeckillGoods findOne(@PathVariable("id") Long id){

        return seckillGoodsService.findOne(id);
    }

    @RequestMapping("/saveOrder/{id}")
    public Result saveOrder(@PathVariable("id") Long id){
        String userId = "jiuwenlong";//本示例未实现登录功能，假设登录用户是jiuwenlong
        return seckillGoodsService.saveOrder(id, userId);
    }


}
