/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.myapp;

import com.codename1.ui.Component;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Stroke;
import com.codename1.ui.Transform;
import com.codename1.ui.geom.GeneralPath;
import com.codename1.ui.geom.Shape;
import com.codename1.ui.layouts.BorderLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author ASUS
 */
public class cloc extends Form{
    
    Form current;
     private Font numbersFont;
    public cloc(Form previous)
    {
        current=this;
         if(Font.isNativeFontSchemeSupported()) {
            numbersFont = Font.createTrueTypeFont("native:MainThin", "native:MainThin");
            numbersFont = numbersFont.derive(Display.getInstance().convertToPixels(3.5f), Font.STYLE_PLAIN);
        } else {
            numbersFont = Font.getDefaultFont();
        }
         
         AnalogClock clock = new AnalogClock();
         clock.start();
         this.add(BorderLayout.CENTER, clock);
         
         
    }
    
     public void stop() {
        current = Display.getInstance().getCurrent();
        if (current instanceof Dialog) {
            ((Dialog) current).dispose();
            current = Display.getInstance().getCurrent();
        }
    }
     
     public class AnalogClock extends Component {
        Date currentTime = new Date();
        long lastRenderedTime = 0;

        @Override
        public boolean animate() {
            if (System.currentTimeMillis() / 1000 != lastRenderedTime / 1000) {
                currentTime.setTime(System.currentTimeMillis());
                return true;
            }
            return false;
        }

        public void start() {
            this.getComponentForm().registerAnimated(this);
        }

        public void stop() {
            this.getComponentForm().deregisterAnimated(this);
        }

        @Override
        public void paintBackground(Graphics g) {
            super.paintBackground(g);

            boolean oldAntialiased = g.isAntiAliased();
            g.setAntiAliased(true);
            double padding = 10;
            double r = Math.min(getWidth(), getHeight()) / 2 - padding;
            double cX = getX() + getWidth() / 2;
            double cY = getY() + getHeight() / 2;

            // draw the ticks
            int tickLen = 10;
            int medTickLen = 30;
            int longTickLen = 50;
            int tickColor = 0xCCCCCC;
            Stroke tickStroke = new Stroke(2f, Stroke.CAP_BUTT, Stroke.JOIN_ROUND, 1f);

            GeneralPath ticksPath = new GeneralPath();

            for (int i = 1; i <= 60; i++) {
                int len = tickLen;
                if (i % 15 == 0) {
                    len = longTickLen;
                } else if (i % 5 == 0) {
                    len = medTickLen;
                }
                double di = (double) i;
                double angleFrom12 = di / 60.0 * 2.0 * Math.PI;
                double angleFrom3 = Math.PI / 2.0 - angleFrom12;
                ticksPath.moveTo(
                        (float) (cX + Math.cos(angleFrom3) * r),
                        (float) (cY - Math.sin(angleFrom3) * r)
                );

                ticksPath.lineTo(
                        (float) (cX + Math.cos(angleFrom3) * (r - len)),
                        (float) (cY - Math.sin(angleFrom3) * (r - len))
                );
            }
            g.setColor(tickColor);
            g.drawShape(ticksPath, tickStroke);

            g.setColor(0x000000);
        
            g.setFont(numbersFont);
            int charHeight = numbersFont.getHeight();
            
            // Draw the numbers
            for (int i = 1; i <= 12; i++) {
                String numStr = "" + i;
                int charWidth = numbersFont.stringWidth(numStr);
                double di = (double) i;
                double angleFrom12 = di / 12.0 * 2.0 * Math.PI;
                double angleFrom3 = Math.PI / 2.0 - angleFrom12;

                int tx = (int) (Math.cos(angleFrom3) * (r - longTickLen));
                int ty = (int) (-Math.sin(angleFrom3) * (r - longTickLen));

                g.translate(
                        tx,
                        ty
                );

                g.drawString(numStr, (int) cX - charWidth / 2, (int) cY - charHeight / 2);
                g.translate(-tx, -ty);

            }

            // Draw the hands of the clock
            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

            GeneralPath secondHand = new GeneralPath();
            secondHand.moveTo((float) cX, (float) cY);
            secondHand.lineTo((float) cX, (float) (cY - (r - medTickLen)));

            Shape translatedSecondHand = secondHand.createTransformedShape(Transform.makeTranslation(0f, 5, 0));

            double second = (double) (calendar.get(Calendar.SECOND));

            double secondAngle = second / 60.0 * 2.0 * Math.PI;

            double absCX = getAbsoluteX() + cX - getX();
            double absCY = getAbsoluteY() + cY - getY();

            g.rotate((float) secondAngle, (int) absCX, (int) absCY);
            g.setColor(0xff0000);
            g.drawShape(translatedSecondHand, new Stroke(2f, Stroke.CAP_BUTT, Stroke.JOIN_BEVEL, 1f));
            g.resetAffine();

            GeneralPath minuteHand = new GeneralPath();
            minuteHand.moveTo((float) cX, (float) cY);
            minuteHand.lineTo((float) cX + 6, (float) cY);
            minuteHand.lineTo((float) cX + 2, (float) (cY - (r - tickLen)));
            minuteHand.lineTo((float) cX - 2, (float) (cY - (r - tickLen)));
            minuteHand.lineTo((float) cX - 6, (float) cY);
            minuteHand.closePath();

            Shape translatedMinuteHand = minuteHand.createTransformedShape(Transform.makeTranslation(0f, 5, 0));

            double minute = (double) (calendar.get(Calendar.MINUTE))
                    + (double) (calendar.get(Calendar.SECOND)) / 60.0;

            double minuteAngle = minute / 60.0 * 2.0 * Math.PI;
            g.rotate((float) minuteAngle, (int) absCX, (int) absCY);
            g.setColor(0x000000);
            g.fillShape(translatedMinuteHand);
            g.resetAffine();

            GeneralPath hourHand = new GeneralPath();
            hourHand.moveTo((float) cX, (float) cY);
            hourHand.lineTo((float) cX + 4, (float) cY);
            hourHand.lineTo((float) cX + 1, (float) (cY - (r - longTickLen) * 0.75));
            hourHand.lineTo((float) cX - 1, (float) (cY - (r - longTickLen) * 0.75));
            hourHand.lineTo((float) cX - 4, (float) cY);
            hourHand.closePath();

            Shape translatedHourHand = hourHand.createTransformedShape(Transform.makeTranslation(0f, 5, 0));

            double hour = (double) (calendar.get(Calendar.HOUR_OF_DAY) % 12)
                    + (double) (calendar.get(Calendar.MINUTE)) / 60.0;

            double angle = hour / 12.0 * 2.0 * Math.PI;
            g.rotate((float) angle, (int) absCX, (int) absCY);
            g.setColor(0x000000);
            g.fillShape(translatedHourHand);
            g.resetAffine();

            g.setAntiAliased(oldAntialiased);
            lastRenderedTime = System.currentTimeMillis();
        }
    }
    
}
