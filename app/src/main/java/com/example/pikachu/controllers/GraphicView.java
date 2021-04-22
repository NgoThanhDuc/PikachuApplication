package com.example.pikachu.controllers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.pikachu.activities.OptionActivity;
import com.example.pikachu.R;
import com.example.pikachu.models.PointLine;

import java.util.ArrayList;
import java.util.Random;

import static com.example.pikachu.activities.MainActivity.backgroundGame;
import static com.example.pikachu.activities.MainActivity.musicPlay;
import static com.example.pikachu.activities.MainActivity.musicCountdownTime;
import static com.example.pikachu.activities.MainActivity.pauseFlag;
import static com.example.pikachu.activities.MainActivity.progressBarStatus;
import static com.example.pikachu.activities.MainActivity.tv_help;
import static com.example.pikachu.activities.MainActivity.tv_level;
import static com.example.pikachu.activities.MainActivity.tv_marks;

public class GraphicView extends View {

    CheckLine checkLine = new CheckLine();
    public static int countHelp = 4;
    public static int LevelGame = 1; // biến LevelGame
    public static int TongDiem = 0;

    public android.os.Handler handler = new Handler();

    int[] backgroundGames = {R.drawable.br1, R.drawable.br2, R.drawable.br3, R.drawable.br4, R.drawable.br5,
            R.drawable.br6, R.drawable.br7, R.drawable.br8, R.drawable.br9, R.drawable.br10};

    //mảng chứa id của các ảnh pokemon trong thư mục drawable
    private int[] ImagePath = {R.drawable.image1, R.drawable.image2, R.drawable.image3,
            R.drawable.image4, R.drawable.image5, R.drawable.image6,
            R.drawable.image7, R.drawable.image8, R.drawable.image9,
            R.drawable.image10, R.drawable.image11, R.drawable.image12,
            R.drawable.image13, R.drawable.image14, R.drawable.image15,
            R.drawable.image16, R.drawable.image17, R.drawable.image18,
            R.drawable.image19, R.drawable.image20, R.drawable.image21,
            R.drawable.image22, R.drawable.image23, R.drawable.image24,
            R.drawable.image25, R.drawable.image26, R.drawable.image27,
            R.drawable.image28, R.drawable.image29, R.drawable.image30,
            R.drawable.image31, R.drawable.image32, R.drawable.image33,
            R.drawable.image34, R.drawable.image35, R.drawable.image36
    };

    final int CARD_NO = 36; //Số lượng hình pikachu (36)
    final int PICTURES_COUNT = 4; //số lần lặp của 1 hình(1 hình xuất hiện 4 lần)

    //ma tran hinh 10x6
    final int GAME_WIDTH = 16; // số cột của mảng game (16)
    final int GAME_HEIGTH = 9; //số dòng của mảng game (9)

    //kích thước của 1 khung chứa thẻ hình
    final int CARD_SIZE_WIDTH = 75;
    final int CARD_SIZE_HEIGTH = 76;

    //khai báo các file nhạc trong game
    MediaPlayer nhacKhongAnDuoc;
    public static MediaPlayer nhacKhiClick;
    MediaPlayer nhacAnDuoc;
    public static MediaPlayer nhacKhiWin;
    public static MediaPlayer nhacKhiLose;

    //Mảng chứa các hình đọc từ  Imagepath(drawable)
    Bitmap[] CardImages;

    //Lưu giá trị của các thẻ hình, vd:[0][0] = 0 nghĩa là có hình tại vị trí [0][0], [0][0] = -1 là không có hình
    public static int[][] CardMatrix;

    //số thẻ còn lại trong ma trận chưa được ẩn
    public static int RemainingCount;

    Boolean CardSelected;  //Thẻ hình thứ nhất đã được chọn

    Boolean DrawPath_ = false; // biến cờ để kiểm tra có vẽ đường nối hai ảnh k, ban đầu k nối là cho false

    int CardX, CardY;  //Vị trí của thẻ hình thứ nhất
    int CurX, CurY;  //Vị trí của thẻ hình thứ 2

    //chiều dài đường đi từ thẻ hình thứ hai đến thẻ hình thứ nhất
    int rCount;

    //đường đi từ thẻ hình thứ hai đến thẻ hình thứ nhất
    // (rX[0],rY[0]) -> (rX[1],rY[1]) -> ...->(rX[rCount],rX[rCount])
    int[] rX, rY;

    //chiều dài đường đi trong quá trình đệ quy
    int tCount;
    int[] tX, tY;

    Direction[] d; //Hướng đi của đường đi tạm

    public GraphicView(Context context) {
        this(context, null);
    }

    public GraphicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraphicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init();
        NewGame();
        InitSound(context);

    }

    //hàm để vẽ
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        //Draw component in here
        DrawBG(canvas);
        if (DrawPath_)
            DrawPath(canvas);

        invalidate();
    }

    //hàm để bắt sự kiện khi người dùng chạm ngón tay vào màn hình, trả ra 2 ô đã được chọn
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int dowx, dowy; //dowx tọa độ điểm thứ 1(x, y), dowy là tọa độ điểm thứ 2(x, y)
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (CardSelected == false) nhacKhiClick.start();
                dowx = (int) (event.getX() / CARD_SIZE_WIDTH);
                dowy = (int) ((event.getY()) / CARD_SIZE_HEIGTH);
                if (dowx >= 0 & dowx <= GAME_WIDTH & dowy >= 0 & dowy <= GAME_HEIGTH)
                    CardClick(dowx, dowy);

                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    public void Init() {
        //Khởi tạo mảng Bitmap
        CardImages = new Bitmap[CARD_NO];

        //tạo và nạp 36 hình vào bitmap
        for (int i = 0; i < CARD_NO; i++) {
            CardImages[i] = BitmapFactory.decodeResource(getResources(), ImagePath[i]);
        }

        //Ma trận lưu trạng thái của thẻ hình(có hình hoặc k có hình)
        CardMatrix = new int[GAME_HEIGTH + 2][GAME_WIDTH + 2];

        //Tạo các mảng tìm đường đi trong quá trình đệ quy (tức các ô trống xung quanh bàn cờ)
        int MAX = (GAME_HEIGTH + 2) * (GAME_WIDTH + 2);
        rX = new int[MAX];
        rY = new int[MAX];
        tX = new int[MAX];
        tY = new int[MAX];
        d = new Direction[MAX];

    }

    public void InitSound(Context c) {
        nhacKhongAnDuoc = MediaPlayer.create(c, R.raw.no);
        nhacKhiClick = MediaPlayer.create(c, R.raw.click);
        nhacAnDuoc = MediaPlayer.create(c, R.raw.pair);
        nhacKhiWin = MediaPlayer.create(c, R.raw.level);
        nhacKhiLose = MediaPlayer.create(c, R.raw.no_move);

    }

    public void NewGame() {
        int i, j, k;
        //bước này là khỏi tạo ra bàn cờ rỗng
        for (i = 0; i <= GAME_HEIGTH + 1; i++)
            for (j = 0; j <= GAME_WIDTH + 1; j++)
                CardMatrix[i][j] = -1;// - 1 tức là cho tất cả các ô đang trống

        for (i = 1; i <= GAME_HEIGTH; i++)
            for (j = 1; j <= GAME_WIDTH; j++)
                CardMatrix[i][j] = -2;//

        //mảng này cho biết mỗi lần xuất hiện mấy lần
        int[] CardCount = new int[CARD_NO];

        //Thiết lập 1 hình xuất hiện 4 lần
        for (k = 0; k < CARD_NO; k++)
            CardCount[k] = PICTURES_COUNT;

        //random và xáo trộn vị trí mỗi các hình
        Random rnd = new Random();

        //duyệt qua từng ô của bàn cờ và thiết lập hình ngẫu nhiên cho ô đó, chừa các ông trống xung quanh
        for (i = 1; i <= GAME_HEIGTH; i++) {
            for (j = 1; j <= GAME_WIDTH; j++) {

                //phần khởi tạo các ảnh pokemon vào từng ô
                do {
                    k = rnd.nextInt(CARD_NO);
                } while (CardCount[k] == 0); //CardCount[k]==0 nghĩa là hình thứ k đã rand đủ 4 lần(tức đã random hết ảnh thứ k) thì dừng
                //cac the hinh can tim k khac
                //lưu giá trị k vào vào mảng
                CardMatrix[i][j] = k;
                CardCount[k]--;//hình thứ k giảm đi 1

            }
        }
        //CardMatrix[2][3]=1;
        RemainingCount = GAME_HEIGTH * GAME_WIDTH;//các thẻ hình chưa được ẩn
        CardSelected = false;//thẻ hình thứ nhất chưa được chọn

    }

    //Thuộc tính để vẽ Rectangle
    private void DrawBG(Canvas canvas) {

        int left, rigth, top, bottom;
        //tao bitmap BG
        Paint paint = new Paint();

        Bitmap bit;

        //GAME_HEIGTH la số dòng của bàn cờ
        //GAME_WIDTH là số cột của bàn cờ
        for (int i = 0; i <= GAME_HEIGTH + 1; i++)
            for (int j = 0; j <= GAME_WIDTH + 1; j++) {

                //tính vị trí và kích thước hình chữ nhật
                left = j * CARD_SIZE_WIDTH;
                top = i * CARD_SIZE_HEIGTH;
                rigth = left + CARD_SIZE_WIDTH;
                bottom = top + CARD_SIZE_HEIGTH;

                //ô không có hình
                if (CardMatrix[i][j] == -1) {

                    paint.setColor(Color.TRANSPARENT);
                    canvas.drawRect(left, top, rigth, bottom, paint);

                } else if (CardMatrix[i][j] == -2) {
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.BLUE);
                    canvas.drawRect(left, top, rigth, bottom, paint);

                    if (CardSelected == true)
                        if (CardX == j && CardY == i) {
                            paint.setColor(Color.YELLOW);
                            //paint.setStrokeWidth(10);
                            canvas.drawRect(left, top, rigth, bottom, paint);
                        }

                } else { // ô có chứa hình
                    bit = CardImages[CardMatrix[i][j]];
                    paint.setColor(Color.TRANSPARENT);
                    canvas.drawRect(left, top, rigth, bottom, paint);
                    /*     canvas.drawBitmap(bit, left, top, null);*/
                    canvas.drawBitmap(bit, null, new RectF(left, top, rigth, bottom), null);

                    //tô màu ô được chọn
                    if (CardSelected == true)
                        if (CardX == j && CardY == i) {
                            paint.setAlpha(80);
                            //paint.setStrokeWidth(10);
                            canvas.drawRect(left, top, rigth, bottom, paint);

                        }
                }
            }
    }

    // đếm xem đường đi tạm rẽ bao nhiêu lần
    private int CountTurn(Direction[] d, int tCount) {
        int count = 0;
        for (int i = 2; i < tCount; i++)    // duyệt qua các điểm trong đường đi
        {
            if (d[i - 1] != d[i]) count++;  // nếu hướng khác nhau nghĩa là có rẽ
        }
        return count;
    }

    //Xử lý đường đi
    private void FindRoute(int x, int y, Direction direct) {
        // không nằm trong phạm vi bàn cờ (CardMatrix) thì thoát
        if (x < 0 || x > GAME_WIDTH + 1) return;
        if (y < 0 || y > GAME_HEIGTH + 1) return;

        if (CardMatrix[y][x] != -1) return;// không phải là ô trống, thoát (1)

        // đưa ô [y,x] vào đường đi
        tX[tCount] = x;
        tY[tCount] = y;

        // ghi nhận là hướng đi đến ô [y,x]
        d[tCount] = direct;

        // nếu rẽ nhiều hơn 2 lần, thoát
        if (CountTurn(d, tCount + 1) > 2) return;

        tCount++;
        CardMatrix[y][x] = -2;              // đánh dấu ô [y,x] đã đi qua

        // lệnh (1) đảm bảo ô đã đi qua sẽ không đi lại nữa
        if (x == CardX && y == CardY)       // nếu đã tìm đến vị trí hình thứ nhất
        {
            // kiểm tra xem đường đi mới tìm được có ngắn hơn đường đi tìm trong các lần trước không
            if (tCount < rCount) {
                // nếu ngắn hơn, ghi nhớ lại đường đi này
                rCount = tCount;
                for (int i = 0; i < tCount; i++) {
                    rX[i] = tX[i];
                    rY[i] = tY[i];
                }
            }
        } else   // nếu chưa đi đến được ô hình thứ nhất -> đệ quy để đi đến 4 ô xung quanh
        {
            FindRoute(x - 1, y, Direction.Left);
            FindRoute(x + 1, y, Direction.Right);
            FindRoute(x, y - 1, Direction.Up);
            FindRoute(x, y + 1, Direction.Down);
        }

        // ô [y,x] đã xét, quay lui nên loại ô [y,x] ra khỏi đường đi
        tCount--;
        // đánh dấu lại là ô [y,x] trống, có thể đi qua lại
        CardMatrix[y][x] = -1;
    }
    //sau khi hàm trên kết thúc ta được các giá trị rCount va rX[],rY[]

    //vẽ đường thẳng nối hai hình
    public void DrawPath(Canvas c) {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.RED);
        p.setStrokeCap(Paint.Cap.ROUND);
        p.setStrokeWidth(10);
        int i, x1, y1, x2, y2;
        x1 = rX[0] * CARD_SIZE_WIDTH + CARD_SIZE_WIDTH / 2;
        y1 = rY[0] * CARD_SIZE_HEIGTH + CARD_SIZE_HEIGTH / 2;     // (x1, y1) là tâm của thẻ hình thứ 2
        for (i = 1; i < rCount; i++) {
            x2 = rX[i] * CARD_SIZE_WIDTH + CARD_SIZE_WIDTH / 2;
            y2 = rY[i] * CARD_SIZE_HEIGTH + CARD_SIZE_HEIGTH / 2; // (x2, y2) là tâm của thẻ hình thứ nhất
            c.drawLine(x1, y1, x2, y2, p);         // Vẽ đường thẳng nối 2 ô
            x1 = x2;
            y1 = y2;
        }

    }

    //hàm xử lý khi click vào Card(từng ô)
    // x - y là vị trí tọa độ của ảnh thứ 2 so với trục tung trục hoành
    // CardX - CardY là tọa độ của ảnh thứ 1 so với trục tung trục hoành
    // CardMatrix[y][x] là tên hình thứ 2, CardMatrix[CardY][CardX] là tên hình thứ 1
    private void CardClick(final int x, int y) {
        if (CardMatrix[y][x] != -1) { // thao tác với thẻ đầu tiên
            if (!CardSelected) {
                CardSelected = true;
                CardX = x;
                CardY = y;


                invalidate();//một cách gọi gián tiếp hàm onDraw, thông báo chế độ xem đã được thay đổi

            } else if (x != CardX || y != CardY) { // xử lý khi click vào 2 ô giống nhau, khác vị trí
                if (CardMatrix[y][x] == CardMatrix[CardY][CardX]) { // kiểm tra 2 ô có tên giống nhau k
                    int temp = CardMatrix[y][x]; // đặt temp bằng tên của ảnh thứ 2 khi click
                    CardMatrix[y][x] = -1;
                    CardMatrix[CardY][CardX] = -1;
                    rCount = Integer.MAX_VALUE;
                    tCount = 0;

                    FindRoute(x, y, Direction.None);//tìm đường đi cho 2 ô đã ấn

                    // tên hình thứ 2 = tên hình thứ 1 = tên của temp(tức hình thứ 2)
                    CardMatrix[y][x] = CardMatrix[CardY][CardX] = temp;

                    if (rCount != Integer.MAX_VALUE) { //nếu tìm thấy đường đi
                        nhacAnDuoc.start();
                        TongDiem += 20;
                        tv_marks.setText("" + TongDiem);


                        // CurdX, CurdY là vị trí tọa độ của ảnh thứ 2 trên trục tung và hoành
                        CurX = x;
                        CurY = y;

                        new Thread(new Runnable() {
                            public void run() {
                                DrawPath_ = true; // cho vẽ đường nối 2 hình
                                postInvalidate(); // vẽ lại trong sự kiện tiếp theo

                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                DrawPath_ = false;

                                //đánh dấu 2 thẻ này đã được ẩn
                                CardMatrix[CurY][CurX] = -1; // ẩn vị trí thẻ thứ 2
                                CardMatrix[CardY][CardX] = -1; // ẩn vị trí thẻ thứ nhất

                                switch (LevelGame) {
                                    case 2:
                                        Left();
                                        break;
                                    case 3:
                                        Right();
                                        break;
                                    case 4:
                                        Top();
                                        break;
                                    case 5:
                                        Bottom();
                                        break;
                                    case 6:
                                        Right();
                                        break;
                                    case 7:
                                        Bottom();
                                        break;
                                    case 8:
                                        Top();
                                        break;
                                    case 9:
                                        Right();
                                        break;
                                    case 10:
                                        Left();
                                        break;
                                    default:
                                        break;
                                }

                                RemainingCount -= 2; //số thẻ chưa được ẩn giảm đi 2
                                CardSelected = false; //tiếp tục chọn con khác

                                if (RemainingCount == 0) { // win

                                    // win
                                    LevelGame++;
                                    musicPlay.stop();
                                    musicCountdownTime.stop();
                                    nhacKhiWin.start();
                                    pauseFlag = true;
                                    countHelp++;
                                    if (LevelGame < 11) {
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                final Dialog dialog = new Dialog(getContext());
                                                dialog.setContentView(R.layout.fragment_win_dialog);

                                                Button btn_nextLevelWin = dialog.findViewById(R.id.btn_nextLevelWin);
                                                TextView tv_marksWin = dialog.findViewById(R.id.tv_marksWin);

                                                tv_marksWin.setText("Scores: " + TongDiem);

                                                btn_nextLevelWin.setOnClickListener(new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        if (!musicPlay.isPlaying()) {
                                                            musicPlay.stop();
                                                            musicPlay = MediaPlayer.create(getContext(), R.raw.music_play);
                                                            musicPlay.start();
                                                        }

                                                        NewGame();
                                                        tv_level.setText("Lv " + LevelGame);
                                                        tv_help.setText("" + countHelp);

                                                        handler.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                backgroundGame.setBackgroundResource(backgroundGames[LevelGame - 1]);
                                                            }
                                                        });

                                                        progressBarStatus = 374000;
                                                        pauseFlag = false;
                                                        invalidate();
                                                        dialog.dismiss();
                                                    }
                                                });
                                                dialog.setCanceledOnTouchOutside(false);
                                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                                dialog.show();
                                                dialog.getWindow().setWindowAnimations(R.style.DialogAnimationWinLosePause);
                                                //Set the dialog to immersive
                                                dialog.getWindow().getDecorView().setSystemUiVisibility(((Activity) getContext()).getWindow().getDecorView().getSystemUiVisibility());
                                                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                            }
                                        });
                                    }

                                    // game complete
                                    if (LevelGame == 11) {

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {

                                                final Dialog dialog = new Dialog(getContext());
                                                dialog.setContentView(R.layout.fragment_win_dialog);

                                                Button btn_nextLevelWin = dialog.findViewById(R.id.btn_nextLevelWin);
                                                TextView tv_marksWin = dialog.findViewById(R.id.tv_marksWin);
                                                TextView tv_win = dialog.findViewById(R.id.tv_win);

                                                tv_win.setText("Congratulations");
                                                tv_marksWin.setText("Scores: " + TongDiem);
                                                btn_nextLevelWin.setText("OK");

                                                btn_nextLevelWin.setOnClickListener(new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        progressBarStatus = 0;
                                                        LevelGame = 1;
                                                        TongDiem = 0;
                                                        countHelp = 4;
                                                        pauseFlag = true;
                                                        musicPlay.stop();

                                                        if (musicPlay.isPlaying()) {
                                                            musicPlay.stop();
                                                        }

                                                        Intent intent = new Intent(((Activity) getContext()), OptionActivity.class);
                                                        ((Activity) getContext()).startActivity(intent);
                                                        dialog.dismiss();
                                                    }
                                                });
                                                dialog.setCanceledOnTouchOutside(false);
                                                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                                dialog.show();
                                                dialog.getWindow().setWindowAnimations(R.style.DialogAnimationWinLosePause);
                                                //Set the dialog to immersive
                                                dialog.getWindow().getDecorView().setSystemUiVisibility(((Activity) getContext()).getWindow().getDecorView().getSystemUiVisibility());
                                                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                                            }
                                        });
                                    }

                                }
                                postInvalidate();
                            }
                        }).start();

                    } else { //nếu không tìm thấy đường đi
                        CardSelected = false;
                        nhacKhongAnDuoc.start();
                        invalidate();
                    }
                } else { //2 thẻ hình không giống nhau
                    CardSelected = false;
                    nhacKhongAnDuoc.start();
                    postInvalidate();
                }
                //ket thuc else dau
            } else if (x == CardX || y == CardY) {  // xử lý khi click vào cùng 1 ô giống nhau, cùng vị trí
                if (CardMatrix[y][x] == CardMatrix[CardY][CardX]) {
                    int temp = CardMatrix[y][x];
                    CardMatrix[y][x] = -1;
                    CardMatrix[CardY][CardX] = -1;
                    CardMatrix[y][x] = CardMatrix[CardY][CardX] = temp;

                    CardSelected = false; //tiếp tục chọn con khác
                }
            }
        }
    }

    //bố trí việc thay đổi mảng ở Level 2
    public void Left() {
        for (int j = CardX; j <= GAME_WIDTH; j++)
            CardMatrix[CardY][j] = CardMatrix[CardY][j + 1];
    }

    //bố trí việc thay đổi mảng ở Level 3
    public void Right() {
        for (int j = CardX; j >= 1; j--)
            CardMatrix[CardY][j] = CardMatrix[CardY][j - 1];
    }

    //bố trí việc thay đổi mảng ở Level 4
    public void Top() {
        for (int i = CardY; i >= 1; i--)
            CardMatrix[i][CardX] = CardMatrix[i - 1][CardX];
    }

    //bố trí việc thay đổi mảng ở Level 5
    public void Bottom() {
        for (int i = CardY; i <= GAME_HEIGTH; i++)
            CardMatrix[i][CardX] = CardMatrix[i + 1][CardX];
    }

    //các hàm liên quan đến việc chơi game
    public void retryGame() {
        RemainingCount = 0;

        if (LevelGame == 1) {
            new Thread(new Runnable() {
                public void run() {
                    DrawPath_ = true;
                    postInvalidate();

                    if (RemainingCount == 0) {

                        TongDiem = 0;
                        CardImages.clone();
                        DrawPath_ = false;

                        tv_marks.setText("" + TongDiem);
                        tv_level.setText("Lv " + LevelGame);

                        progressBarStatus = 374000;
                        pauseFlag = false;
                        backgroundGame.setBackgroundResource(backgroundGames[0]);

                        NewGame();
                    }
                    postInvalidate();

                }
            }).start();

        } else {
            new Thread(new Runnable() {
                public void run() {
                    DrawPath_ = true;
                    postInvalidate();

                    if (RemainingCount == 0) {

                        CardImages.clone();
                        DrawPath_ = false;

                        tv_marks.setText("" + TongDiem);
                        tv_level.setText("Lv " + LevelGame);

                        progressBarStatus = 374000;
                        pauseFlag = false;
                        backgroundGame.setBackgroundResource(backgroundGames[LevelGame - 1]);

                        NewGame();
                    }
                    postInvalidate();

                }
            }).start();
        }
    }

    public void resetGame() {
        RemainingCount = 0;

        if (LevelGame == 1) {
            new Thread(new Runnable() {
                public void run() {
                    DrawPath_ = true;
                    postInvalidate();

                    if (RemainingCount == 0) {

                        TongDiem = 0;
                        CardImages.clone();
                        DrawPath_ = false;

                        tv_marks.setText("" + TongDiem);
                        tv_level.setText("Lv " + LevelGame);

                        progressBarStatus = 374000;
                        pauseFlag = false;
                        backgroundGame.setBackgroundResource(backgroundGames[0]);

                        NewGame();
                    }
                    postInvalidate();

                }
            }).start();

        } else {
            new Thread(new Runnable() {
                public void run() {
                    DrawPath_ = true;
                    postInvalidate();

                    if (RemainingCount == 0) {

                        TongDiem = ((20 * (GAME_WIDTH * GAME_HEIGTH)) / 2) * (LevelGame - 1);

                        CardImages.clone();
                        DrawPath_ = false;

                        tv_marks.setText("" + TongDiem);
                        tv_level.setText("Lv " + LevelGame);

                        progressBarStatus = 374000;
                        pauseFlag = false;
                        backgroundGame.setBackgroundResource(backgroundGames[LevelGame - 1]);

                        NewGame();
                    }
                    postInvalidate();

                }
            }).start();
        }

    }

    public void suggestGame() {

        ArrayList<Point> markCardBasicArr = new ArrayList<Point>();
        ArrayList<Point> markCardArr = new ArrayList<Point>();
        PointLine checkTwoPoint = null;

        Point markCard = new Point(1, 1);
        markCardBasicArr.add(markCard);

        // lấy mảng vị trí các thẻ đầu tiên khác nhau (max 36)
        for (int i = 1; i <= GAME_HEIGTH; i++) {
            if (markCardBasicArr.size() == 36) break;
            for (int j = 1; j <= GAME_WIDTH; j++) {
                int countBasic = 0;
                for (int k = 0; k < markCardBasicArr.size(); k++) {
                    if (CardMatrix[i][j] != -1 && CardMatrix[i][j] != CardMatrix[markCardBasicArr.get(k).x][markCardBasicArr.get(k).y]) {
                        countBasic += 1;
                    }
                }
                if (countBasic == markCardBasicArr.size()) {
                    Point temp = new Point(i, j);
                    markCardBasicArr.add(temp);
                }
                if (markCardBasicArr.size() == 36) break;
            }
        }
        /*  Log.d("XXX", "Size basic " + markCardBasicArr.size());*/

        for (int r = 0; r < markCardBasicArr.size(); r++) {
            if (checkTwoPoint != null) return;

            markCardArr.clear();

            // Lấy vị trí của thẻ đang duyệt (max 4)
            for (int i = 1; i <= GAME_HEIGTH; i++) {
                if (markCardArr.size() == 4) break;
                for (int j = 1; j <= GAME_WIDTH; j++) {
                    if (CardMatrix[i][j] != -1 && CardMatrix[i][j] == CardMatrix[markCardBasicArr.get(r).x][markCardBasicArr.get(r).y]) {
                        Point temp = new Point(i, j);
                        markCardArr.add(temp);
                    }
                    if (markCardArr.size() == 4) break;
                }
            }

            // Kiểm tra có gợi ý thẻ đang duyệt có đường đi không?
            if (markCardArr.size() == 0) {
                /* Log.d("XXX", "KHONG");*/
            } else if (markCardArr.size() == 2) {
                checkTwoPoint = checkLine.checkTwoPoint(markCardArr.get(0), markCardArr.get(1));
                if (checkTwoPoint == null) {
                    /* Log.d("XXX", "KHONG");*/
                } else {

                    //tìm thấy 2 ảnh thõa tất cả điều kiện đường đi trong mảng và tô xanh ảnh lên
                    if (countHelp <= 0) {
                        pauseFlag = true;
                        showDialogNoHelp();

                    } else {
                        countHelp--;
                        tv_help.setText("" + countHelp);

                        CardMatrix.clone();
                        CardMatrix[markCardArr.get(0).x][markCardArr.get(0).y] = -2;
                        CardMatrix.clone();
                        CardMatrix[markCardArr.get(1).x][markCardArr.get(1).y] = -2;
                        CardMatrix.clone();
                    }
                }
                invalidate();

            } else if (markCardArr.size() == 4) {
                for (int i = 0; i < 3; i++) {
                    if (checkTwoPoint != null) break;
                    for (int j = i + 1; j < 4; j++) {
                        checkTwoPoint = checkLine.checkTwoPoint(markCardArr.get(i), markCardArr.get(j));
                        if (checkTwoPoint != null) {

                            //tìm thấy 2 ảnh thõa tất cả điều kiện đường đi trong mảng và tô xanh ảnh lên
                            if (countHelp <= 0) {
                                pauseFlag = true;
                                showDialogNoHelp();
                            } else {
                                countHelp--;
                                tv_help.setText("" + countHelp);

                                CardMatrix.clone();
                                CardMatrix[markCardArr.get(i).x][markCardArr.get(i).y] = -2;
                                CardMatrix.clone();
                                CardMatrix[markCardArr.get(j).x][markCardArr.get(j).y] = -2;
                                CardMatrix.clone();
                            }
                        }
                        break;
                    }
                    if (checkTwoPoint == null)
                        Log.d("XXX", "KHONG");
                }
            }
            invalidate();
        }
    }

    private void showDialogNoHelp() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.fragment_win_dialog);

        Button btn_nextLevelWin = dialog.findViewById(R.id.btn_nextLevelWin);
        TextView tv_marksWin = dialog.findViewById(R.id.tv_marksWin);
        TextView tv_win = dialog.findViewById(R.id.tv_win);

        tv_win.setText("Oops");
        tv_marksWin.setText("Oops, You've used up all your help!");
        btn_nextLevelWin.setText("OK");

        btn_nextLevelWin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pauseFlag = false;
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimationWinLosePause);
        //Set the dialog to immersive
        dialog.getWindow().getDecorView().setSystemUiVisibility(((Activity) getContext()).getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

}

