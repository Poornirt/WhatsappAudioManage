package com.example.whatsappaudiofiles.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappaudiofiles.R;
import com.example.whatsappaudiofiles.jdo.Audio;

import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    private Context mContext;
    private List<Audio> mAudioList;
    private AudioFileClickListener mAudioFileClickListener;
    public AudioAdapter(Context mContext, List<Audio> mAudioList,AudioFileClickListener pAudioFileClickListener) {
        this.mContext = mContext;
        this.mAudioList = mAudioList;
        mAudioFileClickListener= pAudioFileClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_audio_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mNameOfAudio.setText(mAudioList.get(position).getName());
        holder.mDuration.setText(mAudioList.get(position).getDuration());
        holder.mDate.setText(mAudioList.get(position).getDate());
        holder.mAudioFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAudioFileClickListener.onAudioClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAudioList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mNameOfAudio, mDuration, mDate;
        LinearLayout mAudioFile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameOfAudio = itemView.findViewById(R.id.name_of_file);
            mDuration = itemView.findViewById(R.id.duration);
            mDate = itemView.findViewById(R.id.date);
            mAudioFile = itemView.findViewById(R.id.audio_file);
        }
    }

    public interface AudioFileClickListener{
        void onAudioClick(int pPosition);
    }
}
