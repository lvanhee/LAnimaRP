package testing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class SimpleAnimationEngine {

    public static void main(String[] args) {
        new SimpleAnimationEngine();
    }

    public SimpleAnimationEngine() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                } catch (InstantiationException ex) {
                } catch (IllegalAccessException ex) {
                } catch (UnsupportedLookAndFeelException ex) {
                }

                AnimationPane pane = new AnimationPane();

                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLayout(new BorderLayout());
                frame.add(pane);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                pane.init();
                pane.start();
            }

        });
    }

    public static class AnimationPane extends JPanel implements AnimationCanvas {

        private AnimationModel model;

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(400, 400);
        }

        public AnimationModel getModel() {
            return model;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            for (Animatable animatable : getModel().getAnimatables()) {
                animatable.paint(g2d);
            }
            g2d.dispose();
        }

        @Override
        public synchronized void updateState() {

            Runnable update = new Runnable() {
                @Override
                public void run() {
                    AnimationModel model = getModel();
                    for (Animatable animatable : model.getAnimatables()) {
                        animatable.copy();
                    }
                    repaint();
                }

            };

            if (EventQueue.isDispatchThread()) {
                update.run();
            } else {
                try {
                    EventQueue.invokeAndWait(update);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void init() {
            model = new DefaultAnimationModel();
            for (int index = 0; index < 1; index++) {
                model.add(new AnimatableRectangle(this));
            }
            updateState();
        }

        public void start() {
            AnimationEngine engine = new AnimationEngine(this, getModel());
            engine.start();
        }

    }

    public static interface Animatable {

        public void copy();

        public void update(AnimationCanvas canvas, float progress);

        public void paint(Graphics2D g2d);

    }

    public static class AnimationEngine extends Thread {

        private AnimationModel model;
        private AnimationCanvas canvas;

        public AnimationEngine(AnimationCanvas canvas, AnimationModel model) {
            setDaemon(true);
            setName("AnimationThread");
            this.model = model;
            this.canvas = canvas;
        }

        public AnimationCanvas getCanvas() {
            return canvas;
        }

        public AnimationModel getModel() {
            return model;
        }

        @Override
        public void run() {
            float progress = 0;
            long cylceStartTime = System.currentTimeMillis();
            long cylceEndTime = cylceStartTime + 1000;
            int updateCount = 0;
            while (true) {
                long frameStartTime = System.currentTimeMillis();
                getModel().update(getCanvas(), progress);
                getCanvas().updateState();
                long frameEndTime = System.currentTimeMillis();
                long delay = 20 - (frameEndTime - frameStartTime);
                if (delay > 0) {
                    try {
                        sleep(delay);
                    } catch (InterruptedException ex) {
                    }
                }
                long now = System.currentTimeMillis();
                long runtime = now - cylceStartTime;
                progress = (float)runtime / (float)(1000);
                updateCount++;
                if (progress > 1.0) {
                    progress = 0f;
                    cylceStartTime = System.currentTimeMillis();
                    cylceEndTime = cylceStartTime + 1000;
                    System.out.println(updateCount + " updates in this cycle");
                    updateCount = 0;
                }
            }
        }

    }

    public interface AnimationCanvas {

        public void updateState();

        public Rectangle getBounds();

    }

    public static interface AnimationModel {

        public void update(AnimationCanvas canvas, float progress);

        public void add(Animatable animatable);

        public void remove(Animatable animatable);

        public Animatable[] getAnimatables();

    }

    public static class AnimatableRectangle implements Animatable {

        private Rectangle bounds;
        private int dx, dy;
        private Rectangle copyBounds;
        private Color foreground;
        private Color backColor;

        public AnimatableRectangle(AnimationCanvas canvas) {
            bounds = new Rectangle(10, 10);
            Rectangle canvasBounds = canvas.getBounds();
            bounds.x = canvasBounds.x + ((canvasBounds.width - bounds.width) / 2);
            bounds.y = canvasBounds.y + ((canvasBounds.height - bounds.height) / 2);

            dx = (getRandomNumber(10) + 1) - 5;
            dy = (getRandomNumber(10) + 1) - 5;

            dx = dx == 0 ? 1 : dx;
            dy = dy == 0 ? 1 : dy;

            foreground = getRandomColor();
            backColor = getRandomColor();

        }

        protected int getRandomNumber(int range) {
            return (int) Math.round(Math.random() * range);
        }

        protected Color getRandomColor() {
            return new Color(getRandomNumber(255), getRandomNumber(255), getRandomNumber(255));
        }

        @Override
        public void copy() {
            copyBounds = new Rectangle(bounds);
        }

        @Override
        public void update(AnimationCanvas canvas, float progress) {
            bounds.x += dx;
            bounds.y += dy;
            Rectangle canvasBounds = canvas.getBounds();
            if (bounds.x + bounds.width > canvasBounds.x + canvasBounds.width) {
                bounds.x = canvasBounds.x + canvasBounds.width - bounds.width;
                dx *= -1;
            }
            if (bounds.y + bounds.height > canvasBounds.y + canvasBounds.height) {
                bounds.y = canvasBounds.y + canvasBounds.height - bounds.height;
                dy *= -1;
            }
            if (bounds.x < canvasBounds.x) {
                bounds.x = canvasBounds.x;
                dx *= -1;
            }
            if (bounds.y < canvasBounds.y) {
                bounds.y = canvasBounds.y;
                dy *= -1;
            }
        }

        @Override
        public void paint(Graphics2D g2d) {
            g2d.setColor(backColor);
            g2d.fill(copyBounds);
            g2d.setColor(foreground);
            g2d.draw(copyBounds);
        }

    }

    public static class DefaultAnimationModel implements AnimationModel {

        private List<Animatable> animatables;

        public DefaultAnimationModel() {
            animatables = new ArrayList<>(25);
        }

        @Override
        public synchronized void update(AnimationCanvas canvas, float progress) {
            for (Animatable animatable : animatables) {
                animatable.update(canvas, progress);
            }
        }

        @Override
        public synchronized void add(Animatable animatable) {
            animatables.add(animatable);
        }

        @Override
        public synchronized void remove(Animatable animatable) {
            animatables.remove(animatable);
        }

        @Override
        public synchronized Animatable[] getAnimatables() {
            return animatables.toArray(new Animatable[animatables.size()]);
        }

    }

}