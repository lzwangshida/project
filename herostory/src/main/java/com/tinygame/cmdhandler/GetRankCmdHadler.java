package com.tinygame.cmdhandler;

import com.tinygame.msg.GameMsgProtocol;
import com.tinygame.rank.RankItem;
import com.tinygame.rank.RankService;
import io.netty.channel.ChannelHandlerContext;
import java.util.Collections;

public class GetRankCmdHadler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {
    @Override
    public void handler(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {
        if (null == ctx || null == cmd){
            return;
        }


        RankService.getInstance().getRank((rankItemList)->{
            if (null == rankItemList){
                rankItemList = Collections.emptyList();
            }
            GameMsgProtocol.GetRankResult.Builder getRankBuild = GameMsgProtocol.GetRankResult.newBuilder();
            for (RankItem rankItem : rankItemList){
                GameMsgProtocol.GetRankResult.RankItem.Builder rankItemBuild = GameMsgProtocol.GetRankResult.RankItem.newBuilder();
                rankItemBuild.setRankId(rankItem.getRankId());
                rankItemBuild.setUserId(rankItem.getUserId());
                rankItemBuild.setUserName(rankItem.getUserName());
                rankItemBuild.setHeroAvatar(rankItem.getHeroAvatar());
                rankItemBuild.setWin(rankItem.getWin());
                getRankBuild.addRankItem(rankItemBuild);
            }
            GameMsgProtocol.GetRankResult build = getRankBuild.build();

            ctx.writeAndFlush(build);
            return null;
        });

    }
}
