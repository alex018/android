/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mobile.ui.issue;

import static android.graphics.Paint.STRIKE_THRU_TEXT_FLAG;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mobile.R.id;
import com.github.mobile.R.layout;
import com.github.mobile.ui.ItemListAdapter;
import com.github.mobile.util.AvatarUtils;
import com.github.mobile.util.TimeUtils;
import com.github.mobile.util.TypefaceUtils;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryIssue;

/**
 * Adapter to display a list of dashboard issues
 */
public class DashboardIssueListAdapter extends ItemListAdapter<RepositoryIssue, DashboardIssueView> {

    private final AvatarUtils avatarHelper;

    private int numberWidth;

    private final TextView numberView;

    /**
     * Create adapter
     *
     * @param avatarHelper
     * @param inflater
     */
    public DashboardIssueListAdapter(AvatarUtils avatarHelper, LayoutInflater inflater) {
        this(avatarHelper, inflater, null);
    }

    /**
     * Create adapter
     *
     * @param avatarHelper
     * @param inflater
     * @param elements
     */
    public DashboardIssueListAdapter(AvatarUtils avatarHelper, LayoutInflater inflater, RepositoryIssue[] elements) {
        super(layout.dashboard_issue_list_item, inflater);

        this.numberView = (TextView) inflater.inflate(layout.dashboard_issue_list_item, null).findViewById(
                id.tv_issue_number);
        this.avatarHelper = avatarHelper;

        if (elements != null)
            computeNumberWidth(elements);
    }

    private void computeNumberWidth(final Object[] items) {
        int[] numbers = new int[items.length];
        for (int i = 0; i < numbers.length; i++)
            numbers[i] = ((Issue) items[i]).getNumber();
        int digits = TypefaceUtils.getMaxDigits(numbers);
        numberWidth = TypefaceUtils.getWidth(numberView, digits) + numberView.getPaddingLeft()
                + numberView.getPaddingRight();
    }

    @Override
    public ItemListAdapter<RepositoryIssue, DashboardIssueView> setItems(final Object[] items) {
        computeNumberWidth(items);

        return super.setItems(items);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    protected void update(final DashboardIssueView view, final RepositoryIssue issue) {
        view.number.setText(Integer.toString(issue.getNumber()));
        if (issue.getClosedAt() != null)
            view.number.setPaintFlags(view.numberPaintFlags | STRIKE_THRU_TEXT_FLAG);
        else
            view.number.setPaintFlags(view.numberPaintFlags);
        view.number.getLayoutParams().width = numberWidth;

        avatarHelper.bind(view.avatar, issue.getUser());

        String[] segments = issue.getUrl().split("/");
        int length = segments.length;
        if (length >= 4)
            view.repoText.setText(segments[length - 4] + "/" + segments[length - 3]);
        else
            view.repoText.setText("");

        view.pullRequestIcon.setVisibility(issue.getPullRequest().getHtmlUrl() == null ? GONE : VISIBLE);

        view.title.setText(issue.getTitle());
        view.user.setText(issue.getUser().getLogin());
        view.creation.setText(TimeUtils.getRelativeTime(issue.getCreatedAt()));
        view.comments.setText(Integer.toString(issue.getComments()));
    }

    @Override
    protected DashboardIssueView createView(final View view) {
        return new DashboardIssueView(view);
    }
}
