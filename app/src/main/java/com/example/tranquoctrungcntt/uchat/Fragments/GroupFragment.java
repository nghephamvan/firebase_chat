package com.example.tranquoctrungcntt.uchat.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tranquoctrungcntt.uchat.Activities.MainActivity;
import com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants;
import com.example.tranquoctrungcntt.uchat.FragmentAdapters.GroupAdapter;
import com.example.tranquoctrungcntt.uchat.Objects.GroupDetail;
import com.example.tranquoctrungcntt.uchat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.DatabaseNode.CHILD_GROUP_DETAIL;
import static com.example.tranquoctrungcntt.uchat.AppUtils.AppConstants.ROOT_REF;
import static com.example.tranquoctrungcntt.uchat.AppUtils.GetterUtils.getMyFirebaseUserId;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.IntentAction.goToGroupChat;
import static com.example.tranquoctrungcntt.uchat.AppUtils.UserActionUtils.ViewAction.viewGroupDetail;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private RecyclerView rv_groups;
    private ArrayList<GroupDetail> mGroupsList;
    private GroupAdapter mGroupsAdapter;

    private ChildEventListener mGroupChildEvent;
    private Query mGroupRef;

    private ContactFragment fragment_parent;

    private LinearLayout linear_no_group;

    private View rootView;

    public GroupFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_groups, container, false);

        fragment_parent = (ContactFragment) getParentFragment();

        initViews();

        if (mGroupChildEvent != null && mGroupRef != null) mGroupRef.removeEventListener(mGroupChildEvent);

        mGroupRef = ROOT_REF.child(CHILD_GROUP_DETAIL).child(getMyFirebaseUserId());

        mGroupChildEvent = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {


                if (linear_no_group.getVisibility() == View.VISIBLE && rv_groups.getVisibility() == View.GONE) {
                    linear_no_group.setVisibility(View.GONE);
                    rv_groups.setVisibility(View.VISIBLE);
                }

                GroupDetail groupDetail = dataSnapshot.getValue(GroupDetail.class);

                mGroupsList.add(groupDetail);
                mGroupsAdapter.notifyItemInserted(mGroupsList.size() - 1);

                fragment_parent.updateGroupsTitle(mGroupsList.size());

                sortList();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final GroupDetail updatedGroup = dataSnapshot.getValue(GroupDetail.class);

                for (int index = 0; index < mGroupsList.size(); index++) {

                    final GroupDetail matchingGroup = mGroupsList.get(index);

                    if (updatedGroup.getGroupId().equals(matchingGroup.getGroupId())) {

                        if (updatedGroup.getGroupName().equals(matchingGroup.getGroupName())) {

                            mGroupsList.set(index, updatedGroup);
                            mGroupsAdapter.notifyItemChanged(index);

                        } else {

                            mGroupsList.set(index, updatedGroup);
                            mGroupsAdapter.notifyItemChanged(index);

                            sortList();
                        }

                        ((MainActivity) getActivity()).updateConversationGroupDetail(updatedGroup);

                        break;

                    }
                }


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                for (int index = 0; index < mGroupsList.size(); index++) {

                    if (mGroupsList.get(index).getGroupId().equals(dataSnapshot.getKey())) {

                        mGroupsList.remove(index);
                        mGroupsAdapter.notifyItemRemoved(index);
                        mGroupsAdapter.notifyItemRangeChanged(index, mGroupsAdapter.getItemCount());

                        fragment_parent.updateGroupsTitle(mGroupsList.size());

                        if (mGroupsList.isEmpty()) {
                            linear_no_group.setVisibility(View.VISIBLE);
                            rv_groups.setVisibility(View.GONE);
                        }

                        break;

                    }
                }


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mGroupRef.addChildEventListener(mGroupChildEvent);

        mGroupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotConversationId) {

                if (dataSnapshotConversationId.exists()) {

                    linear_no_group.setVisibility(View.GONE);
                    rv_groups.setVisibility(View.VISIBLE);

                } else {

                    linear_no_group.setVisibility(View.VISIBLE);
                    rv_groups.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return rootView;
    }


    private void initViews() {

        mGroupsList = new ArrayList<>();

        mGroupsAdapter = new GroupAdapter(getActivity(), mGroupsList, new AppConstants.AppInterfaces.OnAdapterItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                goToGroupChat(getActivity(), mGroupsList.get(position));
            }

            @Override
            public void OnItemLongClick(View view, int position) {
                viewGroupDetail(getActivity(), mGroupsList.get(position).getGroupId());
            }
        });

        rv_groups = (RecyclerView) rootView.findViewById(R.id.rv_groups);
        rv_groups.setHasFixedSize(true);
        rv_groups.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rv_groups.setAdapter(mGroupsAdapter);
        rv_groups.setItemAnimator(null);

        linear_no_group = (LinearLayout) rootView.findViewById(R.id.linear_no_group);

    }


    private void sortList() {
        Collections.sort(mGroupsList, new Comparator<GroupDetail>() {
            @Override
            public int compare(GroupDetail o1, GroupDetail o2) {
                return Character.compare(o1.getGroupName().charAt(0), o2.getGroupName().charAt(0));
            }
        });

        mGroupsAdapter.notifyItemRangeChanged(0, mGroupsAdapter.getItemCount());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mGroupChildEvent != null && mGroupRef != null) mGroupRef.removeEventListener(mGroupChildEvent);

    }


}
