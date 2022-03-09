package gui;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

/**
 * Implements a {@code Group} with a {@code MeshView} of a {@code TriangleMesh} as a child.
 * The grid is animated by transforming the {@code TriangleMesh}'s Z points with a function using a random point from a normal distribution
 */

class AnimatedGrid extends Group {
    
    private final int gridWidth, gridHeight, cell_size;

    private int frame = 0;
    private boolean switchVar = true;

    AnimatedGrid(final int gridWidth, final int gridHeight, final int cell_size) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cell_size = cell_size;

        Grid3D customGrid = new Grid3D(this.gridWidth, this.gridHeight);
        MeshView mesh = new MeshView(customGrid);
        mesh.setMaterial(new PhongMaterial(Color.CYAN));
        mesh.setDrawMode(DrawMode.LINE);
        mesh.setCullFace(CullFace.NONE);
        this.getChildren().add(mesh);

        this.setRotationAxis(Rotate.X_AXIS);
        this.setRotate(-90);

        AnimationTimer gridAnimation = new AnimationTimer() {
            
            @Override
            public void handle(long now) {
                customGrid.getPoints().clear();
                if (frame < 5) {
                    frame++;
                } else {
                    frame = 0;
                    customGrid.incrementGeneratorStep();
                }
                if (customGrid.getGeneratorStep() >= 10) {
                    customGrid.clearGeneratorStep();
                    if (!switchVar) {
                        customGrid.generateZpoints();
                    }
                    switchVar ^= true;
                }
                if(switchVar) {
                    customGrid.getPoints().addAll(customGrid.nextPointUp());
                } else {
                    customGrid.getPoints().addAll(customGrid.nextPointDown());
                }
            }
        };

        gridAnimation.start();

    }

    private class Grid3D extends TriangleMesh {
        private int gridWidth, gridHeight;
        private final int numOfPoints;
        private final int numOfCells;
        private float[][] ZPointsUp, ZPointsDown;
        private final int frames = 10;
        private int generatorStep = 0;

        Grid3D(int gridWidth, int gridHeight) {
            
            this.gridWidth = gridWidth;
            this.gridHeight = gridHeight;
            numOfPoints = (gridWidth / cell_size + 1) * (gridHeight / cell_size + 1);
            numOfCells = (gridWidth / cell_size) * (gridHeight / cell_size);
            ZPointsUp = new float[numOfPoints * 3][frames]; 
            ZPointsDown = new float[numOfPoints * 3][frames];
            createPoints();
            generateZpoints();

        }
        public void incrementGeneratorStep() {generatorStep++;}
        public void clearGeneratorStep() {generatorStep = 0;}
        public int getGeneratorStep() {return generatorStep;}

        private void createPoints() {
            /**
             * 0    1    2
             * -----------
             * |    |    |
             * |    |    |
             * 3    4    5   
             * -----------
             * |    |    |
             * |    |    |
             * -----------
             * 6    7    8
             */
            final float[] vertices = new float[numOfPoints * 3]; /* 3 coords for each point */
            final float[] texCoords = new float[numOfPoints * 2]; /* 2 coords for each texture point */
            final int[] faces = new int[numOfCells * 12]; /* 6 coords for each face point */
            int idxVertices, idxTex, idxFaces;
            idxVertices = idxTex = idxFaces = 0;
            /**
             * -points-
             * 0 0 0
             * cell_size 0 0
             * 2xcell_size 0 0
             * ...
             * 0 cell_size 0
             * cell_size cell_size 0
             * 2xcell_size cell_size 0
             */
            for(int i = 0; i < gridHeight + cell_size; i += cell_size) {
                for(int j = 0; j < gridWidth + cell_size; j += cell_size ) {
                    vertices[idxVertices] = j;
                    vertices[idxVertices + 1] = i;
                    vertices[idxVertices + 2] = 0;
                    idxVertices += 3;

                    texCoords[idxTex] = (float) j / gridWidth;  /* Normalized */
                    texCoords[idxTex + 1] =  (float) i / gridHeight;
                    idxTex += 2;
                }                                                               
            }
            /**
                     * -Faces-
                     * Counter-clockwise
                     * Ex. faces 0 = {
                     * 0, 0, 3, 3, 1, 1,
                     * 4, 4, 1, 1, 3, 3
                     * }
                     * -----------
                     * |0 / |2 / |
                     * | / 1| / 3|
                     * -----------
                     * |4 / |6 / |
                     * | / 5| / 7|
                     * -----------
                     */
            int skipIdx = 0;
            int topLeft = 0;
            final int oneDown = gridWidth / cell_size + 1;
            for (int i = 0; i < numOfCells; i++){
                if (skipIdx == (int) gridWidth / cell_size) { skipIdx = 0; topLeft++;} /* Skip last vertice of a row */
                /* Top Left triangle */
                faces[idxFaces] = topLeft;
                faces[idxFaces + 1] = faces[idxFaces];
                faces[idxFaces + 2] = topLeft + oneDown;
                faces[idxFaces + 3] = faces[idxFaces + 2];
                faces[idxFaces + 4] = topLeft + 1;
                faces[idxFaces + 5] = faces[idxFaces + 4];
                idxFaces += 6;
                /* Top Right Triangle */
                faces[idxFaces] = topLeft + oneDown + 1;
                faces[idxFaces + 1] = faces[idxFaces];
                faces[idxFaces + 2] = topLeft + 1;
                faces[idxFaces + 3] = faces[idxFaces + 2];
                faces[idxFaces + 4] = topLeft + oneDown;
                faces[idxFaces + 5] = faces[idxFaces + 4];
                idxFaces += 6;
                skipIdx++;
                topLeft++;
            }
            this.getPoints().addAll(vertices);
            this.getTexCoords().addAll(texCoords);
            this.getFaces().addAll(faces);
            return;
        }
        private void generateZpoints() {
            Random rng = new Random();
            float factor = 100 * rng.nextFloat();
            for (int i = 0; i < numOfPoints * 3; i++) {
                double goal = factor * rng.nextGaussian();
                float step = (float) goal / frames;
                ZPointsUp[i][0] = step;
                ZPointsDown[i][0] = (float) goal;
                for (int j = 1; j < frames; j++) {
                    ZPointsUp[i][j] = ZPointsUp[i][j - 1] + step;
                    ZPointsDown[i][j] = ZPointsDown[i][j - 1] - step;
                }
            }
        }
        /**
         * Calculates the Z points of the grid's vertices ascending animation for the next frame.
         */
        public float[] nextPointUp() {
            final int numOfPoints = (gridWidth / cell_size + 1) * (gridHeight / cell_size + 1);
            final float[] vertices = new float[numOfPoints * 3]; /* 3 coords for each point */
            int idxVertices = 0;
            for(int i = 0; i < gridHeight + cell_size; i += cell_size) {
                for(int j = 0; j < gridWidth + cell_size; j += cell_size ) {
                    vertices[idxVertices] = j;
                    vertices[idxVertices + 1] = i;
                    vertices[idxVertices + 2] = ZPointsUp[(i / cell_size) * (j / cell_size)][generatorStep];
                    idxVertices += 3;
                }
            }
            return vertices;
        }

        /**
         * Calulates the Z points of the grid's vertices descending animation for the next frame.
         */

        public float[] nextPointDown() {
            final int numOfPoints = (gridWidth / cell_size + 1) * (gridHeight / cell_size + 1);
            final float[] vertices = new float[numOfPoints * 3]; /* 3 coords for each point */
            int idxVertices = 0;
            for(int i = 0; i < gridHeight + cell_size; i += cell_size) {
                for(int j = 0; j < gridWidth + cell_size; j += cell_size ) {
                    vertices[idxVertices] = j;
                    vertices[idxVertices + 1] = i;
                    vertices[idxVertices + 2] = ZPointsDown[(i / cell_size) * (j / cell_size)][generatorStep];
                    idxVertices += 3;
                }
            }
            return vertices;
        }
    }
}
