package com.kms.katalon.core.webui.driver.safari;

import java.util.concurrent.BlockingQueue;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

/**
 * This class copy code from CSafariDriverPipelineFactory class to overcome it's package-protected fields
 * This class should not be re-factor
 * TODO: This class should be change when our com.kms.katalon.selenium-standalone project is updated
 *
 */
public class CSafariDriverPipelineFactory implements ChannelPipelineFactory {
    private final int port;

    private final BlockingQueue<CWebSocketConnection> connectionQueue;

    private final ChannelGroup channelGroup;

    CSafariDriverPipelineFactory(int port, BlockingQueue<CWebSocketConnection> connectionQueue,
            ChannelGroup channelGroup) {
        this.port = port;
        this.connectionQueue = connectionQueue;
        this.channelGroup = channelGroup;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("connection handler", new ConnectionHandler());
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpChunkAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("handler", new CSafariDriverChannelHandler(port, connectionQueue));
        return pipeline;
    }

    private class ConnectionHandler extends SimpleChannelUpstreamHandler {

        @Override
        public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
            channelGroup.add(e.getChannel());
            ctx.sendUpstream(e);
        }
    }
}
