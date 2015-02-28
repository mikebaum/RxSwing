package rx.swing.sources;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.SwingScheduler;

public class ItemEventSourceTester
{
    public static void main( String[] args ) throws InvocationTargetException, InterruptedException
    {
        final AtomicReference<JPanel> panelRef = new AtomicReference<JPanel>();
        SwingUtilities.invokeAndWait( new Runnable()
        {
            @Override
            public void run()
            {
                JFrame frame = new JFrame();
                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                
                JPanel panel = new JPanel( new BorderLayout() );
                panel.add( new JButton( "Click me" ) );
                
                panelRef.set( panel );
                
                frame.add( panel );
                frame.pack();
                frame.setVisible( true );
            }
        } );
        
        
        new Thread( new Runnable()
        {
            @Override
            public void run()
            {
                System.err.println("about to change the container");
                Observable.just( "Don't click me" ).subscribeOn( SwingScheduler.getInstance() ).subscribe( new Action1<String>() {
                    @Override
                    public void call( String t1 )
                    {
                        System.err.println( "is event thread: " + SwingUtilities.isEventDispatchThread() );
                        JPanel panel = panelRef.get();
                        //panel.add( new JLabel( "I'm a label" ), BorderLayout.NORTH );
                        
                        panel.add( new JLabel( t1 ), BorderLayout.NORTH );
                        
                        panel.addComponentListener( new ComponentAdapter()
                        {
                            public void componentResized(ComponentEvent e) 
                            {
                                System.err.println("resized");
                            };
                        } );
                        
                        panel.revalidate();
                        panel.repaint();
                    }} );
            }
        } ).start();
    }
}
