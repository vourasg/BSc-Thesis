package com.icsd.municipapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

class CommentListAdapter extends ArrayAdapter<CommentList> {
    CommentListAdapter(Context context, ArrayList<CommentList> comments) {
        super(context, 0, comments);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        final CommentList commentList = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if(commentList.level == 0)
        {
            parent.setPaddingRelative(0,0,0,0);
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_row, parent, false);
            ImageView user_image = (ImageView) convertView.findViewById(R.id.user_image);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            TextView username = (TextView) convertView.findViewById(R.id.username);
            TextView comment_text = (TextView) convertView.findViewById(R.id.comment_text);
            convertView.findViewById(R.id.report_comment).setVisibility(View.GONE);
            convertView.findViewById(R.id.delete_comment).setVisibility(View.GONE);
            // Populate the data into the template view using the data object
            user_image.setImageDrawable(null);
            date.setText(commentList.date);
            username.setText(commentList.username);
            comment_text.setText(commentList.comment_text);
        }
        else if (commentList.level == 1)
        {
            // Lookup view for data population
            parent.setPaddingRelative(0,0,0,0);
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_row, parent, false);
            ImageView user_image = (ImageView) convertView.findViewById(R.id.user_image);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            TextView username = (TextView) convertView.findViewById(R.id.username);
            TextView comment_text = (TextView) convertView.findViewById(R.id.comment_text);

            if (commentList.user_id == ((vars) getContext().getApplicationContext()).getDBuserID()) {
                Holder.holder.delete = (Button) convertView.findViewById(R.id.delete_comment);
                convertView.findViewById(R.id.report_comment).setVisibility(View.INVISIBLE);
                Holder.holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new actionComment().execute(getContext().getApplicationContext(), "deleteComment", commentList.comment_id, v);
                    }
                });
            } else {
                Holder.holder.report = (Button) convertView.findViewById(R.id.report_comment);
                convertView.findViewById(R.id.delete_comment).setVisibility(View.INVISIBLE);
                Holder.holder.report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new actionComment().execute(getContext().getApplicationContext(), "reportComment", commentList.comment_id, v);
                    }
                });
            }
            Holder.holder.comment = (Button) convertView.findViewById(R.id.comment);
            Holder.holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext().getApplicationContext(), AddComment.class);
                    intent.putExtra("action", "subComment");
                    intent.putExtra("report_id", commentList.comment_id);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    getContext().getApplicationContext().startActivity(intent);
                }
            });

            // Populate the data into the template view using the data object
            if(null==commentList.user_image)
                user_image.setImageDrawable(convertView.getResources().getDrawable(R.drawable.profile_image));
            else
                user_image.setImageBitmap(commentList.user_image);
            date.setText(commentList.date);
            username.setText(commentList.username);
            comment_text.setText(commentList.comment_text);

        }
        else
        {
            // Lookup view for data population
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_row_tabbed, parent, false);
            ImageView user_image = (ImageView) convertView.findViewById(R.id.user_image);
            TextView date = (TextView) convertView.findViewById(R.id.date);
            TextView username = (TextView) convertView.findViewById(R.id.username);
            TextView comment_text = (TextView) convertView.findViewById(R.id.comment_text);

            if (commentList.user_id == ((vars) getContext().getApplicationContext()).getDBuserID()) {
                Holder.holder.delete = (Button) convertView.findViewById(R.id.delete_comment);
                convertView.findViewById(R.id.report_comment).setVisibility(View.INVISIBLE);
                Holder.holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new actionComment().execute(getContext().getApplicationContext(), "deleteComment", commentList.comment_id, v);
                    }
                });
            } else {
                Holder.holder.report = (Button) convertView.findViewById(R.id.report_comment);
                convertView.findViewById(R.id.delete_comment).setVisibility(View.INVISIBLE);
                Holder.holder.report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new actionComment().execute(getContext().getApplicationContext(), "reportComment", commentList.comment_id, v);
                    }
                });
            }

            // Populate the data into the template view using the data object
            if(null==commentList.user_image)
                user_image.setImageDrawable(convertView.getResources().getDrawable(R.drawable.profile_image));
            else
                user_image.setImageBitmap(commentList.user_image);
            date.setText(commentList.date);
            username.setText(commentList.username);
            comment_text.setText(commentList.comment_text);
        }
        // Return the completed view to render on screen
        return convertView;
    }


    private class actionComment extends AsyncTask<Object, Void, Object[]> {
        @Override
        protected Object[] doInBackground(Object... objects) {
            Context context = (Context) objects[0];
            Object[] return_object = new Object[3];
            return_object[0] = context;
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                out.writeObject("RECONNECT");
                out.flush();
                out.writeObject(((vars)context).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {

                    out.writeObject(objects[1]);
                    out.flush();
                    out.writeObject(objects[2]);
                    out.flush();

                    return_object[1] = in.readObject();
                }
                else{

                }

            } catch (Exception e) {
                e.printStackTrace();
                return_object[1] = null;
            }

            return_object[2] = objects[3];

            return return_object;
        }

        @Override
        protected void onPostExecute(Object[] objects) {
            Snackbar.make((View) objects[2], (String) objects[1], Snackbar.LENGTH_LONG).show();
        }
    }

}

