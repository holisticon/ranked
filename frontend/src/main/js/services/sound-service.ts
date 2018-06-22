export namespace SoundService {
    const goalClickSound = new Audio('sounds/goal-click.wav');

    export function playGoalSound(): void {
        goalClickSound.play();
    }
}
