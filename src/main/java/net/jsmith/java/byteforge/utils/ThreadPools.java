package net.jsmith.java.byteforge.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

import javafx.application.Platform;

public class ThreadPools {
	
	public static Executor PLATFORM   = new PlatformExecutor( );
	public static Executor BACKGROUND = Executors.newCachedThreadPool( new DaemonThreadFactory( ) );

	public static CompletableFuture< Void > runBackground( Runnable runnable ) {
		return CompletableFuture.runAsync( runnable, ThreadPools.BACKGROUND );
	}
	
	public static < T > CompletableFuture< T > supplyBackground( Supplier< T > supplier ) {
		return CompletableFuture.supplyAsync( supplier, ThreadPools.BACKGROUND );
	}
	
	private static class PlatformExecutor implements Executor {

		@Override
		public void execute( Runnable command ) {
			Platform.runLater( command );
		}
		
	}
	
	private static class DaemonThreadFactory implements ThreadFactory {
		
		private final ThreadFactory defaultFactory;
		public DaemonThreadFactory( ) {
			this.defaultFactory = Executors.defaultThreadFactory( );
		}
		@Override
		public Thread newThread( Runnable r ) {
			Thread nThread = this.defaultFactory.newThread( r );
			nThread.setDaemon( true );
			
			return nThread;
		}
		
	}
	
}
