import com.atlassian.bitbucket.commit.CommitService
import com.atlassian.bitbucket.content.AbstractChangeCallback
import com.atlassian.bitbucket.content.Change
import com.atlassian.bitbucket.content.ChangesRequest
import com.atlassian.bitbucket.content.ContentService
import com.atlassian.bitbucket.repository.Repository
import com.atlassian.sal.api.component.ComponentLocator

if (!isSquashCommit()) {
    return true
}

String nextCommitId = event.addedCommits.commits.first().id
String prevCommitId = event.previousFromHash

Repository repository = event.pullRequest.commits.first().repository
List<String> paths = collectPaths(repository, nextCommitId)

FileLoader fileLoader = new FileLoader(repository: repository)

return paths.any { path ->
    Properties prevFile = fileLoader.load(path, prevCommitId)
    Properties nextFile = fileLoader.load(path, nextCommitId)

    return prevFile.any {
        if (nextFile.get(it.key) != it.value) {
            return true
        }
        false
    }
}


// utils

boolean isSquashCommit() {
    event.addedCommits.commits.size() == 1 && event.removedCommits.commits.size() > 0
}

class FileLoader {
    Repository repository
    ContentService contentService = ComponentLocator.getComponent(ContentService)

    Properties load(String path, String commitId) {
        def outputStream = new ByteArrayOutputStream()
        contentService.streamFile(repository, commitId, path, { outputStream })
        def properties = new Properties()
        return properties.load(new ByteArrayInputStream(outputStream.toByteArray()))
    }
}

List<String> collectPaths(Repository repository, String nextCommitId) {
    ChangesRequest changesRequest = new ChangesRequest.Builder(repository, nextCommitId).build()
    def commitService = ComponentLocator.getComponent(CommitService)
    ChangedPathsCollector changedPathsCollector = new ChangedPathsCollector()
    commitService.streamChanges(changesRequest, changedPathsCollector)
    return changedPathsCollector.getChangedPaths()
}

class ChangedPathsCollector extends AbstractChangeCallback {
    List<String> changedPaths = []

    @Override
    boolean onChange(Change change) {
        changedPaths.add(change.getPath().toString());
        return true
    }

    List<String> getChangedPaths() {
        return changedPaths
    }
}
