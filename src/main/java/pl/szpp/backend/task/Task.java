package pl.szpp.backend.task;

import pl.szpp.backend.igc.file.IgcFile;

public interface Task {

    TaskResult calculate(IgcFile igc);

}
