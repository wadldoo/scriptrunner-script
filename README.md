# Withdraw approvals when a pull request changed except for squash 
## Background
### 1
Rebase and squash keep git commits history clean.
However, it's often cumbersome to do in the case of a pull request.
To be on the safe side, we may want to withdraw approval because the commit after approval may have changed the content.
However, if we undo all commits, then squash is included.
We will have to ask the reviewers to approve again, even though the file itself has not changed.
The adaptavist scriptrunner has a feature called "Withdraw approvals when a pull request changed", and it's customizable.
This script tries to customize the conditions of it to keep approval in case of squash.

### 2
Understanding the documentation around PullRequestRescopedEvent usage was hard, I hope this sample will help it.

# Features
- Check if commit is squash or not
- Collect changed paths
- If there are no diff, keep approval

 
# Usage
Copy and paste to script form

 
# Note
- This would only work at the admin level, not at the sandbox-constrained repository level
- The performance impact to bitbucket server may be significant
 

 
