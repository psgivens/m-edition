package com.PhillipScottGivens;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.PhillipScottGivens.Led.LedMatrix;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: psgivens
 * Date: 5/25/12
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class LedDrawActivity extends Activity {

    private LedMatrix leds;
    private ColorHistory colorHistory = new ColorHistory();
    private Button changeColorButton;
    private int colorForDialog = Color.BLACK;

    final int COLOR_TABLE[] = {
            0xffffffff, 0xffc0c0c0, 0xff808080, 0xff000000,
            0xffffc0c0, 0xffff6060, 0xffff0000, 0xff800000,
            0xffffe0c0, 0xffffb060, 0xffff8000, 0xff804000,
            0xffffffc0, 0xffffff60, 0xffffff00, 0xff808000,
            0xffe0ffc0, 0xffb0ff60, 0xff80ff00, 0xff408000,
            0xffc0ffc0, 0xff60ff60, 0xff00ff00, 0xff008000,
            0xffc0ffe0, 0xff60ffb0, 0xff00ff80, 0xff008040,
            0xffc0ffff, 0xff60ffff, 0xff00ffff, 0xff008080,
            0xffc0e0ff, 0xff60b0ff, 0xff0080ff, 0xff004480,
            0xffc0c0ff, 0xff6060ff, 0xff0000ff, 0xff000080,
            0xffe0c0ff, 0xffb060ff, 0xff8000ff, 0xff400080,
            0xffffc0ff, 0xffff60ff, 0xffff00ff, 0xff800080,
            0xffffc0e0, 0xffff60b0, 0xffff0080, 0xff800040
    };

    LayoutInflater inflater;
    View dialogView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Copy this code to somewhere useful. I may need it again.
//        setContentView(R.layout.led_draw_master);
//        LedDrawPagerPanel adapter = new LedDrawPagerPanel();
//        ViewPager myPager = (ViewPager) findViewById(R.id.ledDrawPagerPanel);
//        myPager.setAdapter(adapter);
//        myPager.setCurrentItem(2);

        setContentView(R.layout.led_draw_view);
        leds = (LedMatrix)findViewById(R.id.ledMatrix);

        int initialColor = Color.WHITE;
        leds.setColor(initialColor);
        colorHistory.addColor(initialColor);

        Button clearAllButton = (Button)findViewById(R.id.clearAllButton);
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leds.clear();
            }
        });

        changeColorButton = (Button)findViewById(R.id.chooseColorButton);
        changeColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorChooserDialog();
            }
        });

        final Button colorHistoryButton = (Button)findViewById(R.id.changeBackgroundColorButton);
        colorHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSelectorDialog();
            }
        });
    }

    public interface OnColorChosenListener
    {
        public void onColorChosen(int color);
    }
    public interface OnColorChangingListener
    {
        public void onColorChanging(int color);
    }

    public class ColorHistory
    {
        private static final int ColorCount = 20;
        final Vector history = new Vector();
        private final int count = 0;
        public ColorHistory()
        {
            for (int index=0;index<ColorCount;index++)
                history.add(Color.BLACK);
        }
        public void addColor(int color)
        {
            for(int index=0;index<0;index++)
                if ((Integer)history.get(index) == color)
                {
                    // touchColor calls addColor but it will remove
                    // the color before it does, so this code will not
                    // be called recursively.
                    touchColor(index);
                    return;
                }

            history.insertElementAt(color, 0);
            if (history.size() > ColorCount)
                history.remove(ColorCount);
        }
        public int getColor(int index)
        {
            return (Integer)history.get(index);
        }
        public void touchColor(int index)
        {
            if (index == 0)
                return;

            Integer color = (Integer)history.remove(index);
            addColor(color);
        }
        public Vector getHistory()
        {
            return history;
        }
    }
    public class ColorSeekBarHandler implements OnSeekBarChangeListener
    {
        private final SeekBar seekBar;
        private OnColorChosenListener colorChosenListener;
        private OnColorChangingListener colorChangingListener;
        public ColorSeekBarHandler(SeekBar seekBar, int color, int initialColor)
        {
            this.seekBar = seekBar;
            seekBar.setOnSeekBarChangeListener(this);
            seekBar.setProgress(initialColor);
        }

        public void setOnColorChanging(OnColorChangingListener listener)
        {
            this.colorChangingListener = listener;
            updateColor();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

            updateColor();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            updateColor();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateColor();
        }

        private void updateColor()
        {
            int value = seekBar.getProgress();
            if (colorChangingListener != null)
                colorChangingListener.onColorChanging(value);
        }
    }

    private void colorChooserDialog() {
        colorForDialog = leds.getColor();

        final String rString = getResources().getString(R.string.color_picker_red);
        final String gString = getResources().getString(R.string.color_picker_green);
        final String bString = getResources().getString(R.string.color_picker_blue);

        inflater = LayoutInflater.from(this);
        dialogView = inflater.inflate(R.layout.dialog_picker, null);

        int mColor = leds.getColor();
        final TextView viewer = (TextView)dialogView.findViewById(R.id.colorPickerViewer);
        viewer.setBackgroundColor(mColor);
        viewer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

        final TextView textR = (TextView)dialogView.findViewById(R.id.colorPickerTextR);
        final TextView textG = (TextView)dialogView.findViewById(R.id.colorPickerTextG);
        final TextView textB = (TextView)dialogView.findViewById(R.id.colorPickerTextB);
        textR.setText(String.format("%s(%02X)", rString, (mColor & 0x00ff0000) >> 16));
        textG.setText(String.format("%s(%02X)", gString, (mColor & 0x0000ff00) >> 8));
        textB.setText(String.format("%s(%02X)", bString, (mColor & 0x000000ff)));

        final SeekBar seekBarR = (SeekBar)dialogView.findViewById(R.id.colorPickerSeekBarR);
        final SeekBar seekBarG = (SeekBar)dialogView.findViewById(R.id.colorPickerSeekBarG);
        final SeekBar seekBarB = (SeekBar)dialogView.findViewById(R.id.colorPickerSeekBarB);
        final ColorSeekBarHandler redSeekBarHandler
                = new ColorSeekBarHandler(seekBarR, 0x00ff0000, (mColor & 0x00ff0000) >> 16);
        final ColorSeekBarHandler greenSeekBarHandler
                = new ColorSeekBarHandler(seekBarG, 0x0000FF00, (mColor & 0x0000ff00) >> 8);
        final ColorSeekBarHandler blueSeekBarHandler
                = new ColorSeekBarHandler(seekBarB, 0x000000FF, (mColor & 0x000000ff));

        redSeekBarHandler.setOnColorChanging(new OnColorChangingListener() {
            @Override
            public void onColorChanging(int color) {
               textR.setText(String.format("%s(%02X)", rString, color));
                colorForDialog = (colorForDialog & 0XFF00FFFF) | color << 16;
                viewer.setBackgroundColor(colorForDialog);
            }
        });
        greenSeekBarHandler.setOnColorChanging(new OnColorChangingListener() {
            @Override
            public void onColorChanging(int color) {
                textG.setText(String.format("%s(%02X)", gString, color));
                colorForDialog = (colorForDialog & 0XFFFF00FF) | color << 8;
                viewer.setBackgroundColor(colorForDialog);
            }
        });
        blueSeekBarHandler.setOnColorChanging(new OnColorChangingListener() {
            @Override
            public void onColorChanging(int color) {
                textB.setText(String.format("%s(%02X)", bString, color));
                colorForDialog = (colorForDialog & 0XFFFFFF00) | color;
                viewer.setBackgroundColor(colorForDialog);
            }
        });

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(dialogView);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int idx) {
                colorHistory.addColor(colorForDialog);
                changeColorButton.setBackgroundColor(colorForDialog);
                leds.setColor(colorForDialog);
            }
        });
        alert.show();
    }

    protected void colorSelectorDialog() {
        final String rString = getResources().getString(R.string.color_picker_red);
        final String gString = getResources().getString(R.string.color_picker_green);
        final String bString = getResources().getString(R.string.color_picker_blue);

        LayoutInflater inflater1 = LayoutInflater.from(this);
        final View dialogView1 = inflater1.inflate(R.layout.dialog_selecter, null);

        final GridView gridView = (GridView)dialogView1.findViewById(R.id.colorSelectGridView);

        ArrayAdapter<Integer> adapter
                = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, colorHistory.getHistory()) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView)super.getView(position, convertView, parent);
                int color = super.getItem(position);
                view.setBackgroundColor(color);
                view.setText("");
                return view;
            }
        };

        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                int mColor = colorHistory.getColor(arg2);
                colorHistory.touchColor(arg2);
                leds.setColor(mColor);

                viewDialog.dismiss();
            }
        });

        viewDialog = new AlertDialog.Builder(this)
                .setView(dialogView1)
                .setNegativeButton("Cancel", null)
                .create();
        viewDialog.show();
    }
    Dialog viewDialog;

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == 0) {
            final String rString = getResources().getString(R.string.color_picker_red);
            final String gString = getResources().getString(R.string.color_picker_green);
            final String bString = getResources().getString(R.string.color_picker_blue);

            LayoutInflater inflater1 = LayoutInflater.from(this);
            final View dialogView1 = inflater1.inflate(R.layout.dialog_selecter, null);

            final GridView gridView = (GridView)dialogView1.findViewById(R.id.colorSelectGridView);

            ArrayAdapter<Integer> adapter
                    = new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, colorHistory.getHistory()) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView view = (TextView)super.getView(position, convertView, parent);
                    int color = super.getItem(position);
                    view.setBackgroundColor(color);
                    view.setText("");
                    return view;
                }
            };

            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {
                    int mColor = colorHistory.getColor(arg2);
                    colorHistory.touchColor(arg2);
                    leds.setColor(mColor);

                    final TextView textR = (TextView)dialogView.findViewById(R.id.colorPickerTextR);
                    final TextView textG = (TextView)dialogView.findViewById(R.id.colorPickerTextG);
                    final TextView textB = (TextView)dialogView.findViewById(R.id.colorPickerTextB);

                    textR.setText(String.format("%s(%02X)", rString, (mColor & 0x00ff0000) >> 16));
                    textG.setText(String.format("%s(%02X)", gString, (mColor & 0x0000ff00) >> 8));
                    textB.setText(String.format("%s(%02X)", bString, (mColor & 0x000000ff)));

                    final TextView viewer = (TextView)dialogView.findViewById(R.id.colorPickerViewer);
                    viewer.setBackgroundColor(mColor);

                    final SeekBar seekBarR = (SeekBar)dialogView.findViewById(R.id.colorPickerSeekBarR);
                    final SeekBar seekBarG = (SeekBar)dialogView.findViewById(R.id.colorPickerSeekBarG);
                    final SeekBar seekBarB = (SeekBar)dialogView.findViewById(R.id.colorPickerSeekBarB);
                    seekBarR.setProgress((mColor & 0x00ff0000) >>16);
                    seekBarG.setProgress((mColor & 0x0000ff00) >> 8);
                    seekBarB.setProgress((mColor & 0x000000ff));

                    dismissDialog(0);
                }
            });

            return new AlertDialog.Builder(this)
                    .setView(dialogView1)
                    .setNegativeButton("Cancel", null)
                    .create();
        }
        return super.onCreateDialog(id);
    }
}